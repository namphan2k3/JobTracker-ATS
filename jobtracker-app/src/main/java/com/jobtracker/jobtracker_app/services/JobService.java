package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.job.*;
import com.jobtracker.jobtracker_app.dto.responses.job.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    JobSummaryResponse create(JobCreationRequest request);
    JobResponse getById(String id);
    Page<JobSummaryResponse> getAllJobByCompany(JobFilterRequest request, Pageable pageable);
    Page<PublicJobListResponse> getPublicJobs(PublicJobFilterRequest request, Pageable pageable);
    PublicJobDetailResponse getPublicJobById(String id);
    JobUpdateResponse update(String id, JobUpdateRequest request);
    JobUpdateStatusResponse updateStatus(String id, JobUpdateStatusRequest request);
    void delete(String id);
    List<JobSkillResponse> getJobSkills(String jobId);
    JobSkillCreationResponse addSkillToJob(JobSkillCreationRequest request, String jobId);
    JobSkillResponse updateJobSkill(String jobId, String skillId, JobSkillUpdateRequest request);
    void deleteJobSkill(String jobId, String skillId);
}




