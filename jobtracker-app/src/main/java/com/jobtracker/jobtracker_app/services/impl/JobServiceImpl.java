package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.job.*;
import com.jobtracker.jobtracker_app.dto.responses.job.*;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.JobMapper;
import com.jobtracker.jobtracker_app.mappers.JobSkillMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.JobService;
import com.jobtracker.jobtracker_app.services.PlanLimitService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;
    JobMapper jobMapper;
    UserRepository userRepository;
    CompanyRepository companyRepository;
    JobSkillRepository jobSkillRepository;
    JobSkillMapper jobSkillMapper;
    SkillRepository skillRepository;
    SecurityUtils securityUtils;
    PlanLimitService planLimitService;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_CREATE')")
    public JobSummaryResponse create(JobCreationRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        planLimitService.enforceJobLimit(currentUser.getCompany().getId());
        Job job = jobMapper.toJob(request);
        job.setUser(currentUser);
        job.setCompany(currentUser.getCompany());
        return jobMapper.toJobSummaryResponse(jobRepository.save(job));
    }

    @Override
    @PreAuthorize("hasAuthority('JOB_READ')")
    public JobResponse getById(String id) {
        Job job = getJobForCurrentCompanyOrThrow(id);
        return jobMapper.toJobResponse(job);
    }

    @Override
    @PreAuthorize("hasAuthority('JOB_READ')")
    public Page<JobSummaryResponse> getAllJobByCompany(JobFilterRequest request, Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        String companyId = user.getCompany().getId();
        return jobRepository.searchJobs(companyId,
                request.getJobStatus(),
                request.getIsRemote(),
                request.getSearch(),
                pageable).map(jobMapper::toJobSummaryResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public JobUpdateResponse update(String id, JobUpdateRequest request) {
        Job job = getJobForCurrentCompanyOrThrow(id);
        jobMapper.updateJob(job, request);

        return jobMapper.toJobUpdateResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public JobUpdateStatusResponse updateStatus(String id, JobUpdateStatusRequest request) {
        Job job = getJobForCurrentCompanyOrThrow(id);

        if(request.getJobStatus() != null){
            if(job.getJobStatus() == JobStatus.DRAFT && request.getJobStatus() == JobStatus.PUBLISHED){
                job.setJobStatus(JobStatus.PUBLISHED);
                job.setPublishedAt(LocalDateTime.now());
            }
            else if(job.getJobStatus() == JobStatus.PUBLISHED && request.getJobStatus() == JobStatus.DRAFT){
                job.setJobStatus(JobStatus.DRAFT);
            }
        }

        jobRepository.save(job);

        return JobUpdateStatusResponse.builder()
                .jobStatus(job.getJobStatus())
                .publishedAt(job.getPublishedAt())
                .expiresAt(job.getExpiresAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_DELETE')")
    public void delete(String id) {
        Job job = getJobForCurrentCompanyOrThrow(id);
        job.softDelete();
        jobRepository.save(job);
    }

    @Override
    public Page<PublicJobListResponse> getPublicJobs(PublicJobFilterRequest request, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        return jobRepository.searchPublishedJobs(
                request != null ? request.getCompanyId() : null,
                request != null ? request.getJobType() : null,
                request != null ? request.getIsRemote() : null,
                request != null ? request.getLocation() : null,
                request != null ? request.getSearch() : null,
                today,
                now,
                pageable
        ).map(jobMapper::toPublicJobListResponse);
    }

    @Override
    public PublicJobDetailResponse getPublicJobById(String id) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        Job job = jobRepository.findPublishedJobById(id, today, now)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));
        List<PublicJobSkillResponse> skills = jobSkillRepository.findByJobIdWithSkill(job.getId())
                .stream()
                .map(jobSkillMapper::toPublicJobSkillResponse)
                .toList();
        return jobMapper.toPublicJobDetailResponse(job, skills);
    }

    @Override
    @PreAuthorize("hasAuthority('JOB_READ')")
    public List<JobSkillResponse> getJobSkills(String jobId) {
        getJobForCurrentCompanyOrThrow(jobId);
        return jobSkillRepository.findByJobIdWithSkill(jobId)
                .stream().map(jobSkillMapper::toJobSkillResponse).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public JobSkillCreationResponse addSkillToJob(JobSkillCreationRequest request, String jobId) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(request.getSkillId())
                .filter(Skill::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        Job job = getJobForCurrentCompanyOrThrow(jobId);

        if(jobSkillRepository.existsByJob_IdAndSkill_Id(jobId, request.getSkillId())){
            throw new AppException(ErrorCode.JOB_SKILL_EXISTED);
        }

        JobSkill jobSkill = jobSkillMapper.toJobSkill(request);
        jobSkill.setSkill(skill);
        jobSkill.setJob(job);

        return jobSkillMapper.toJobSkillCreationResponse(jobSkillRepository.save(jobSkill));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public JobSkillResponse updateJobSkill(String jobId, String skillId, JobSkillUpdateRequest request) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(skillId)
                .filter(Skill::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        Job job = getJobForCurrentCompanyOrThrow(jobId);

        JobSkill jobSkill = jobSkillRepository.findByJob_IdAndSkill_Id(jobId, skillId)
                .orElseThrow(()-> new AppException(ErrorCode.JOB_SKILL_NOT_EXISTED));

        if(request.getIsRequired() != null){
            jobSkill.setIsRequired(request.getIsRequired());
        }

        if(request.getProficiencyLevel() != null && !request.getProficiencyLevel().isBlank()){
            jobSkill.setProficiencyLevel(request.getProficiencyLevel());
        }

        jobSkillRepository.save(jobSkill);

        return jobSkillMapper.toJobSkillResponse(jobSkill);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public void deleteJobSkill(String jobId, String skillId) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(skillId)
                .filter(Skill::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        Job job = getJobForCurrentCompanyOrThrow(jobId);

        if(!jobSkillRepository.existsByJob_IdAndSkill_Id(jobId, skillId)){
            throw new AppException(ErrorCode.JOB_SKILL_NOT_EXISTED);
        }

        JobSkill jobSkill = jobSkillRepository.findByJob_IdAndSkill_Id(jobId, skillId)
                        .orElseThrow(()-> new AppException(ErrorCode.JOB_SKILL_NOT_EXISTED));

        jobSkill.softDelete();
        jobSkillRepository.save(jobSkill);
    }

    private Job getJobForCurrentCompanyOrThrow(String jobId) {
        User currentUser = securityUtils.getCurrentUser();
        String companyId = currentUser.getCompany().getId();
        return jobRepository.findByIdAndCompany_IdAndDeletedAtIsNull(jobId, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));
    }
}




