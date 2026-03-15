package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.job.JobSkillWithName;
import com.jobtracker.jobtracker_app.dto.requests.application.*;
import com.jobtracker.jobtracker_app.dto.responses.application.*;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.enums.StatusType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.ApplicationMapper;
import com.jobtracker.jobtracker_app.mappers.ApplicationStatusHistoryMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.ApplicationService;
import com.jobtracker.jobtracker_app.services.CVScoringService;
import com.jobtracker.jobtracker_app.services.PlanLimitService;
import com.jobtracker.jobtracker_app.services.EmailService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import com.jobtracker.jobtracker_app.validator.file.impl.PdfFileValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationServiceImpl implements ApplicationService {
    PdfFileValidator pdfFileValidator;
    Cloudinary cloudinary;
    JobRepository jobRepository;
    CVScoringService cvScoringService;
    JobSkillRepository jobSkillRepository;
    ObjectMapper objectMapper;
    ApplicationRepository applicationRepository;
    ApplicationStatusRepository applicationStatusRepository;
    AttachmentRepository attachmentRepository;
    UserRepository userRepository;
    ApplicationStatusHistoryRepository applicationStatusHistoryRepository;
    SecurityUtils securityUtils;
    ApplicationMapper applicationMapper;
    ApplicationStatusHistoryMapper applicationStatusHistoryMapper;
    EmailService emailService;
    PlanLimitService planLimitService;

    @Override
    @Transactional
    public void ApplyToJob(ApplyToJobRequest request, String jobId) throws IOException {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        planLimitService.enforceApplicationLimit(job.getCompany().getId());

        ApplicationStatus newStatus =
                applicationStatusRepository
                        .findByCompany_IdAndIsDefaultTrueAndDeletedAtIsNull(job.getCompany().getId())
                        .orElseGet(() ->
                                applicationStatusRepository
                                        .findByCompanyIsNullAndIsDefaultTrueAndDeletedAtIsNull()
                                        .orElseThrow(() ->
                                                new AppException(ErrorCode.DEFAULT_STATUS_NOT_CONFIGURED)
                                        )
                        );

        pdfFileValidator.validate(request.getResume());

        String applicationToken = UUID.randomUUID().toString();

        String folderPath = "jobtracker_ats/applications/" + applicationToken + "/cv";

        Map<?,?> result = cloudinary.uploader().upload(request.getResume().getBytes(),
                ObjectUtils.asMap(
                        "use_filename", true,
                        "unique_filename", true,
                        "folder", folderPath,
                        "type", "upload",
                        "resource_type", "image"
                ));


        if(result.get("asset_id") == null || result.get("secure_url") == null){
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        String extractText = extractText(request.getResume().getInputStream());

        List<JobSkillWithName> jobSkillWithNames = jobSkillRepository.findSkillsByJobId(jobId);

        ApplicationScoringResult scoreResult = cvScoringService.score(extractText,jobSkillWithNames);

        MatchedSkillsJson matchedSkillsJson = MatchedSkillsJson.builder()
                .matchedRequired(scoreResult.getMatchedRequiredSkills())
                .missingRequired(scoreResult.getMissingRequiredSkills())
                .matchedOptional(scoreResult.getMatchedOptionalSkills())
                .missingOptional(scoreResult.getMissingOptionalSkills())
                .build();

        // To JSON
        String matchedSkills = objectMapper.writeValueAsString(matchedSkillsJson);

        User assignTo = userRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(job.getCreatedBy(), job.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Application application = Application.builder()
                .job(job)
                .company(job.getCompany())
                .candidateName(request.getCandidateName())
                .candidateEmail(request.getCandidateEmail())
                .candidatePhone(request.getCandidatePhone())
                .coverLetter(request.getCoverLetter())
                .status(newStatus)
                .applicationToken(applicationToken)
                .resumeFilePath((String) result.get("secure_url"))
                .appliedDate(LocalDate.now())
                .extractedText(extractText)
                .matchScore(scoreResult.getMatchScore())
                .matchedSkills(matchedSkills)
                .assignedTo(assignTo)
                .build();

        String publicId = (String) result.get("public_id");

        Application saved = null;

        try{
            saved = applicationRepository.save(application);

            job.setApplicationsCount(job.getApplicationsCount() + 1);
            jobRepository.save(job);

            emailService.sendApplicationConfirmation(application);
        } catch (Exception e) {
            // rollback file nếu DB fail
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "image", "type", "upload"));
            throw e;
        }
    }

    @Override
    @Transactional
    public UploadAttachmentsResponse UploadAttachments(UploadAttachmentsRequest request, String applicationToken)
            throws IOException {
        Application application = applicationRepository.findByApplicationTokenAndDeletedAtIsNull(applicationToken)
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        boolean allowedByStatus = application.getStatus().getStatusType().equals(StatusType.SCREENING) ||
                        application.getStatus().getStatusType().equals(StatusType.INTERVIEW);

        if (!application.getAllowAdditionalUploads() && !allowedByStatus) {
            throw new AppException(ErrorCode.UPLOAD_NOT_ALLOWED);
        }

        String folderPath = "jobtracker_ats/applications/" + applicationToken + "/attachment";

        Map<?,?> result = cloudinary.uploader().upload(request.getFile().getBytes(),
                ObjectUtils.asMap(
                        "use_filename", true,
                        "unique_filename", true,
                        "folder", folderPath,
                        "type", "upload",
                        "resource_type", "image"
                ));

        if(result.get("asset_id") == null || result.get("secure_url") == null) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        String publicId = (String) result.get("public_id");

        Attachment attachment = Attachment.builder()
                .id(publicId)
                .application(application)
                .filename((String) result.get("display_name"))
                .originalFilename((String) result.get("original_filename"))
                .filePath((String) result.get("secure_url"))
                .fileSize((Long) result.get("bytes"))
                .fileType(request.getFile().getContentType())
                .attachmentType(request.getAttachmentType())
                .description(request.getDescription())
                .uploadedAt(LocalDateTime.now())
                .build();

        Attachment saved = null;

        try{
            saved = attachmentRepository.save(attachment);
        } catch (Exception e) {
            // rollback file nếu DB fail
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "image", "type", "upload"));
            throw e;
        }

        return UploadAttachmentsResponse.builder()
                .id(attachment.getId())
                .applicationId(attachment.getApplication().getId())
                .fileName(attachment.getFilename())
                .attachmentType(attachment.getAttachmentType())
                .fileSize(attachment.getFileSize())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }

    @Override
    public TrackStatusResponse TrackStatus(String applicationToken) {
        Application application = applicationRepository.findByApplicationTokenAndDeletedAtIsNull(applicationToken)
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        ApplicationStatusDetail statusDetail = ApplicationStatusDetail.builder()
                .name(application.getStatus().getName())
                .displayName(application.getStatus().getDisplayName())
                .color(application.getStatus().getColor())
                .build();

        return TrackStatusResponse.builder()
                .id(application.getId())
                .jobTitle(application.getJob().getTitle())
                .candidateName(application.getCandidateName())
                .candidateEmail(application.getCandidateEmail())
                .status(statusDetail)
                .appliedDate(application.getAppliedDate())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_UPDATE')")
    public AssignApplicationResponse AssignApplication(String id, AssignApplicationRequest request) {
        User user = userRepository.findById(request.getAssignedTo())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, user.getCompany().getId())
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        application.setAssignedTo(user);
        applicationRepository.save(application);

        return AssignApplicationResponse.builder()
                .id(application.getId())
                .assignedTo(application.getAssignedTo().getId())
                .assignedToName(application.getAssignedTo().getLastName())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public Page<ApplicationResponse> getApplications(ApplicationFilterRequest filter, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Page<Application> applications = applicationRepository.searchApplications(
                currentUser.getCompany().getId(),
                filter.getStatus(),
                filter.getJobId(),
                filter.getAssignedTo(),
                filter.getSearch(),
                filter.getMinMatchScore(),
                filter.getMaxMatchScore(),
                pageable);
        return applications.map(app -> applicationMapper.toApplicationResponse(app, objectMapper));
    }

    @Override
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public ApplicationResponse getApplicationById(String id) {
        User currentUser = securityUtils.getCurrentUser();
        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));
        return applicationMapper.toApplicationResponse(application, objectMapper);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_CREATE')")
    public ApplicationResponse createApplication(ApplicationCreateRequest request) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(request.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        planLimitService.enforceApplicationLimit(job.getCompany().getId());

        ApplicationStatus status = applicationStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        User assignTo = userRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(job.getCreatedBy(), job.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Application application = Application.builder()
                .job(job)
                .company(job.getCompany())
                .candidateName(request.getCandidateName())
                .candidateEmail(request.getCandidateEmail())
                .candidatePhone(request.getCandidatePhone())
                .status(status)
                .source(request.getSource())
                .appliedDate(request.getAppliedDate())
                .coverLetter(request.getCoverLetter())
                .notes(request.getNotes())
                .assignedTo(assignTo)
                .build();

        application = applicationRepository.save(application);

        job.setApplicationsCount(job.getApplicationsCount() + 1);
        jobRepository.save(job);

        return applicationMapper.toApplicationResponse(application, objectMapper);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_UPDATE')")
    public UpdateApplicationStatusResponse updateStatus(String id,
                                                        ApplicationUpdateStatusRequest request) {

        User currentUser = securityUtils.getCurrentUser();

        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(
                        id,
                        currentUser.getCompany().getId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        ApplicationStatus currentStatus = application.getStatus();

        ApplicationStatus newStatus = applicationStatusRepository
                .findActiveStatus(
                        request.getStatusId(),
                        currentUser.getCompany().getId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        boolean shouldSendEmail = isSendEmail(request, currentStatus, newStatus);

        if (shouldSendEmail) {
            if (newStatus.getStatusType().equals(StatusType.OFFER)) {
                emailService.sendManualOffer(application, request.getOfferRequest());
            }

            if (newStatus.getStatusType().equals(StatusType.REJECTED)) {
                emailService.sendCandidateRejected(application, request.getCustomMessage());
            }

            if (newStatus.getStatusType().equals(StatusType.HIRED)) {
                emailService.sendCandidateHired(application, request.getCustomMessage());
            }
        }

        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(application)
                .fromStatus(currentStatus)
                .toStatus(newStatus)
                .changedBy(currentUser)
                .notes(request.getNotes())
                .build();

        applicationStatusHistoryRepository.save(history);

        application.setStatus(newStatus);

        return UpdateApplicationStatusResponse.builder()
                .id(application.getId())
                .statusId(newStatus.getId())
                .previousStatus(currentStatus.getName())
                .notes(request.getNotes())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private static boolean isSendEmail(ApplicationUpdateStatusRequest request,
                                       ApplicationStatus currentStatus,
                                       ApplicationStatus newStatus) {
        StatusType currentType = currentStatus.getStatusType();
        StatusType newType = newStatus.getStatusType();

        // Không cho chuyển từ terminal
        if (currentType.isTerminal()) {
            throw new AppException(ErrorCode.APPLICATION_STATUS_IS_TERMINAL);
        }

        // Không cho chuyển về chính nó
        if (currentStatus.getId().equals(newStatus.getId())) {
            throw new AppException(ErrorCode.APPLICATION_STATUS_SAME);
        }

        // Validate business lifecycle bằng order
        if (!currentType.canMoveTo(newType)) {
            throw new AppException(ErrorCode.APPLICATION_STATUS_INVALID_TRANSITION);
        }

        Boolean autoSendEmail = newStatus.getAutoSendEmail();
        return request.getSendEmail() != null
                ? request.getSendEmail()
                : Boolean.TRUE.equals(autoSendEmail);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_UPDATE')")
    public ApplicationResponse updateApplication(String id, ApplicationUpdateRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        applicationMapper.updateApplication(application, request);
        applicationRepository.save(application);
        return applicationMapper.toApplicationResponse(application, objectMapper);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_DELETE')")
    public void deleteApplication(String id) {
        User currentUser = securityUtils.getCurrentUser();
        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        application.softDelete();
        applicationRepository.save(application);
    }

    @Override
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public List<ApplicationStatusHistoryResponse> ApplicationStatusHistory(String id) {
        User currentUser = securityUtils.getCurrentUser();
        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        List<ApplicationStatusHistory> histories =
                applicationStatusHistoryRepository.findByApplication_IdOrderByCreatedAtDesc(application.getId());

        return histories.stream().map(applicationStatusHistoryMapper::toResponse).toList();
    }

    private String extractText(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try(PDDocument pdDocument = Loader.loadPDF(bytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdDocument);
        }
    }
}
