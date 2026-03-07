package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.dto.requests.email.*;
import com.jobtracker.jobtracker_app.dto.responses.email.*;
import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailType;
import com.jobtracker.jobtracker_app.enums.SystemRole;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.mappers.EmailTemplateMapper;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.InterviewRepository;
import com.jobtracker.jobtracker_app.repositories.EmailTemplateRepository;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import com.jobtracker.jobtracker_app.services.EmailTemplateService;
import com.jobtracker.jobtracker_app.services.TemplateRenderer;
import com.jobtracker.jobtracker_app.services.email.EmailVariableResolver;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailTemplateServiceImpl implements EmailTemplateService {

    EmailTemplateRepository emailTemplateRepository;
    EmailOutboxService emailOutboxService;
    TemplateRenderer templateRenderer;
    EmailVariableResolver emailVariableResolver;
    ApplicationRepository applicationRepository;
    InterviewRepository interviewRepository;
    SecurityUtils securityUtils;
    EmailTemplateMapper emailTemplateMapper;
    ObjectMapper objectMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EMAIL_TEMPLATE_CREATE')")
    public EmailTemplateResponse create(EmailTemplateCreationRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        String companyId = currentUser.getCompany().getId();

        if (emailTemplateRepository.existsByCodeAndCompany_IdAndDeletedAtIsNull(request.getCode(), companyId)) {
            throw new AppException(ErrorCode.EMAIL_TEMPLATE_EXISTED);
        }

        EmailTemplate template = emailTemplateMapper.toEntity(request);
        template.setCompany(currentUser.getCompany());
        if (template.getIsActive() == null) {
            template.setIsActive(true);
        }
        if (request.getVariables() != null && !request.getVariables().isEmpty()) {
            try {
                template.setVariables(objectMapper.writeValueAsString(request.getVariables()));
            } catch (Exception e) {
                template.setVariables("[]");
            }
        }
        template = emailTemplateRepository.save(template);
        return emailTemplateMapper.toResponse(template, objectMapper);
    }

    @Override
    @PreAuthorize("hasAuthority('EMAIL_TEMPLATE_READ')")
    public EmailTemplateDetailResponse getById(String id) {
        EmailTemplate template = getTemplateForCurrentCompanyOrThrow(id);
        return emailTemplateMapper.toDetailResponse(template, objectMapper);
    }

    @Override
    @PreAuthorize("hasAuthority('EMAIL_TEMPLATE_READ')")
    public Page<EmailTemplateResponse> getAll(String code, String name, Boolean isActive, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        String companyId = currentUser.getCompany().getId();

        return emailTemplateRepository.searchTemplates(companyId, code, name, isActive, pageable)
                .map(t -> emailTemplateMapper.toResponse(t, objectMapper));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EMAIL_TEMPLATE_UPDATE')")
    public EmailTemplateResponse update(String id, EmailTemplateUpdateRequest request) {
        User user = securityUtils.getCurrentUser();
        EmailTemplate template = getTemplateForCurrentCompanyOrThrow(id);

        if (template.isGlobal() && !user.getRole().getName().equals(SystemRole.SYSTEM_ADMIN.name())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        emailTemplateMapper.updateEntity(template, request);
        if (request.getVariables() != null) {
            try {
                template.setVariables(objectMapper.writeValueAsString(request.getVariables()));
            } catch (Exception e) {
                template.setVariables("[]");
            }
        }
        template = emailTemplateRepository.save(template);
        return emailTemplateMapper.toResponse(template, objectMapper);
    }

    @Override
    @Transactional
    public void delete(String id) {
        EmailTemplate template = getTemplateForCurrentCompanyOrThrow(id);
        if (template.isGlobal()) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        template.softDelete();
        emailTemplateRepository.save(template);
    }

    @Override
    @PreAuthorize("hasAuthority('EMAIL_TEMPLATE_READ')")
    public EmailTemplatePreviewResponse preview(String id, EmailTemplatePreviewRequest request) {
        EmailTemplate template = getTemplateForCurrentCompanyOrThrow(id);
        Map<String, Object> variables = resolveVariables(template, request);
        String subject = templateRenderer.render(template.getSubject(), variables);
        String htmlContent = templateRenderer.render(template.getHtmlContent(), variables);
        return EmailTemplatePreviewResponse.builder()
                .subject(subject)
                .htmlContent(htmlContent)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EMAIL_TEMPLATE_UPDATE')")
    public void sendTest(String id, EmailTemplateSendTestRequest request) {
        EmailTemplate template = getTemplateForCurrentCompanyOrThrow(id);
        User currentUser = securityUtils.getCurrentUser();
        String toEmail = request != null && request.getToEmail() != null && !request.getToEmail().isBlank()
                ? request.getToEmail()
                : currentUser.getEmail();

        EmailContext context = EmailContext.builder()
                .companyId(currentUser.getCompany().getId())
                .userId(currentUser.getId())
                .applicationId(null)
                .manualValues(null)
                .build();

        EmailType emailType;
        try {
            emailType = EmailType.valueOf(template.getCode());
        } catch (Exception e) {
            throw new AppException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND);
        }

        SendEmailRequest sendRequest = SendEmailRequest.builder()
                        .templateCode(emailType)
                        .companyId(currentUser.getCompany().getId())
                        .aggregateType(AggregateType.USER)
                        .aggregateId(currentUser.getId())
                        .recipientEmail(toEmail)
                        .recipientName(currentUser.getFirstName() + " " + currentUser.getLastName())
                        .replyToEmail(currentUser.getEmail())
                        .replyToName((currentUser.getFirstName() + " " + currentUser.getLastName()).trim())
                        .context(context)
                        .build();

        emailOutboxService.createEmailOutbox(sendRequest);
    }

    private EmailTemplate getTemplateForCurrentCompanyOrThrow(String id) {
        User currentUser = securityUtils.getCurrentUser();
        String companyId = currentUser.getCompany().getId();

        return emailTemplateRepository.findByIdAndCompany_IdAndDeletedAtIsNull(id, companyId)
                .or(() -> emailTemplateRepository.findByIdAndCompanyIsNullAndDeletedAtIsNull(id))
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND));
    }

    private Map<String, Object> resolveVariables(EmailTemplate template, EmailTemplatePreviewRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        if (request.getSampleData() != null && !request.getSampleData().isEmpty()) {
            return request.getSampleData();
        }
        User currentUser = securityUtils.getCurrentUser();
        String companyId = currentUser.getCompany().getId();

        if (request.getApplicationId() != null) {
            return applicationRepository.findByIdAndCompany_IdWithJobAndStatus(request.getApplicationId(), companyId)
                    .map(app -> {
                        EmailContext ctx = EmailContext.builder()
                                .applicationId(app.getId())
                                .companyId(app.getCompany() != null ? app.getCompany().getId() : null)
                                .jobId(app.getJob() != null ? app.getJob().getId() : null)
                                .build();
                        return emailVariableResolver.buildAllVariables(ctx);
                    })
                    .orElse(Collections.emptyMap());
        }
        if (request.getInterviewId() != null) {
            return interviewRepository.findByIdAndCompany_IdAndDeletedAtIsNull(request.getInterviewId(), companyId)
                    .map(interview -> {
                        EmailContext ctx = EmailContext.builder()
                                .applicationId(interview.getApplication() != null ? interview.getApplication().getId() : null)
                                .interviewId(interview.getId())
                                .companyId(interview.getCompany() != null ? interview.getCompany().getId() : null)
                                .build();
                        return emailVariableResolver.buildAllVariables(ctx);
                    })
                    .orElse(Collections.emptyMap());
        }
        return Collections.emptyMap();
    }

}
