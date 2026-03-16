package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.attachment.AttachmentUploadRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobSkillWithName;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationScoringResult;
import com.jobtracker.jobtracker_app.dto.responses.application.MatchedSkillsJson;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentResponse;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Attachment;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.AttachmentType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.AttachmentMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.AttachmentService;
import com.jobtracker.jobtracker_app.services.CVScoringService;
import com.jobtracker.jobtracker_app.services.PdfExtractionService;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttachmentServiceImpl implements AttachmentService {
    AttachmentRepository attachmentRepository;
    AttachmentMapper attachmentMapper;
    ApplicationRepository applicationRepository;
    CompanyRepository companyRepository;
    UserRepository userRepository;
    Cloudinary cloudinary;
    FileValidator pdfFileValidator;
    PdfExtractionService pdfExtractionService;
    JobSkillRepository jobSkillRepository;
    CVScoringService cvScoringService;
    ObjectMapper objectMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ATTACHMENT_CREATE')")
    public AttachmentCreationResponse uploadAttachment(String applicationId,
                                                       AttachmentUploadRequest request) throws IOException {
        pdfFileValidator.validate(request.getFile());

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(applicationId, user.getCompany().getId())
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        String folderPath = "jobtracker_ats/applications/" + applicationId + "/cv";

        Map<?,?> result = cloudinary.uploader().upload(request.getFile().getBytes(),
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

        String publicId = (String) result.get("public_id");

        if(request.getAttachmentType() == AttachmentType.RESUME){
            String extractText = pdfExtractionService.extractText(request.getFile().getInputStream());

            List<JobSkillWithName> jobSkillWithNames = jobSkillRepository.findSkillsByJobId(application.getJob().getId());

            ApplicationScoringResult scoreResult = cvScoringService.score(extractText,jobSkillWithNames);

            MatchedSkillsJson matchedSkillsJson = MatchedSkillsJson.builder()
                    .matchedRequired(scoreResult.getMatchedRequiredSkills())
                    .missingRequired(scoreResult.getMissingRequiredSkills())
                    .matchedOptional(scoreResult.getMatchedOptionalSkills())
                    .missingOptional(scoreResult.getMissingOptionalSkills())
                    .build();

            // To JSON
            String matchedSkills = objectMapper.writeValueAsString(matchedSkillsJson);

            application.setResumeFilePath((String) result.get("secure_url"));
            application.setMatchScore(scoreResult.getMatchScore());
            application.setExtractedText(extractText);
            application.setMatchedSkills(matchedSkills);

            applicationRepository.save(application);
        }

        Attachment attachment = Attachment.builder()
                .application(application)
                .company(user.getCompany())
                .user(user)
                .filename((String) result.get("display_name"))
                .originalFilename((String) result.get("original_filename"))
                .filePath((String) result.get("secure_url"))
                .publicId(publicId)
                .fileSize(((Number) result.get("bytes")).longValue())
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

        return attachmentMapper.toAttachmentCreationResponse(saved);
    }

    @Override
    @PreAuthorize("hasAuthority('ATTACHMENT_READ')")
    public URI downloadAttachment(String id) {

        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED));

        if (!attachment.getCompany().getId().equals(user.getCompany().getId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String signedUrl = cloudinary.url()
                .resourceType("image")
                .type("upload")
                .signed(true)
                .transformation(new Transformation().flags("attachment"))
                .generate(attachment.getPublicId());


        return URI.create(signedUrl);
        }


    @Override
    @PreAuthorize("hasAuthority('ATTACHMENT_READ')")
    public List<AttachmentResponse> getApplicationAttachments(String applicationId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean exists = applicationRepository.existsByIdAndCompany_Id(applicationId, user.getCompany().getId());

        if (!exists) {
            throw new AppException(ErrorCode.APPLICATION_NOT_EXISTED);
        }

        return attachmentRepository
                .findByApplication_IdAndCompany_IdAndDeletedAtIsNull(applicationId, user.getCompany().getId())
                .stream().map(attachmentMapper::toAttachmentResponse).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ATTACHMENT_DELETE')")
    public void delete(String id) throws IOException {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED));

        Map result = cloudinary.uploader().destroy(attachment.getPublicId(),
                ObjectUtils.asMap(
                        "resource_type", "image"));

        if ("ok".equals(result.get("result"))) {
            attachmentRepository.deleteById(id);
        } else {
            throw new AppException(ErrorCode.DELETE_FILE_FAILED);
        }
    }
}
