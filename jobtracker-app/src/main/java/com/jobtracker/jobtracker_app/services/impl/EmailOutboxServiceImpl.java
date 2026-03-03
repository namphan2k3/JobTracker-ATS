package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.email.SendEmailRequest;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryDetailResponse;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.EmailOutbox;
import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.enums.EmailType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.EmailOutboxMapper;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.EmailOutboxRepository;
import com.jobtracker.jobtracker_app.repositories.EmailTemplateRepository;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import com.jobtracker.jobtracker_app.services.TemplateRenderer;
import com.jobtracker.jobtracker_app.services.email.EmailVariableResolver;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class EmailOutboxServiceImpl implements EmailOutboxService {
    EmailTemplateRepository emailTemplateRepository;
    EmailOutboxRepository emailOutboxRepository;
    CompanyRepository companyRepository;
    TemplateRenderer templateRenderer;
    EmailVariableResolver emailVariableResolver;
    EmailOutboxMapper emailOutboxMapper;
    SecurityUtils securityUtils;

    @Value("${brevo.email-system}")
    @NonFinal
    String emailSystem;

    @Value("${brevo.email-system-name}")
    @NonFinal
    String emailSystemName;

    @Override
    @Transactional
    public void createEmailOutbox(SendEmailRequest request) {
        EmailTemplate template = emailTemplateRepository
                .findByCodeAndCompany_IdAndIsActiveTrueAndDeletedAtIsNull(request.getTemplateCode().toString(), request.getCompanyId())
                .orElseGet(() ->
                        emailTemplateRepository
                                .findByCodeAndCompanyIsNullAndIsActiveTrueAndDeletedAtIsNull(request.getTemplateCode().toString())
                                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND))
                );

        Map<String, Object> variables = emailVariableResolver.buildAllVariables(request.getContext());
        if (request.getContext() != null && request.getContext().getManualValues() != null) {
            variables.putAll(request.getContext().getManualValues());
        }

        String subject = templateRenderer.render(template.getSubject(), variables);
        String html = templateRenderer.render(template.getHtmlContent(), variables);

        Company company = companyRepository.findByIdAndDeletedAtIsNull(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));

        AggregateType aggregateType = request.getAggregateType() != null ? request.getAggregateType() : AggregateType.APPLICATION;
        String aggregateId = request.getAggregateId() != null ? request.getAggregateId() : "";

        EmailOutbox emailOutbox = EmailOutbox.builder()
                .emailType(request.getTemplateCode())
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .company(company)
                .toEmail(request.getRecipientEmail())
                .toName(request.getRecipientName())
                .fromEmail(emailSystem)
                .fromName(template.getFromName() != null ? template.getFromName() : emailSystemName)
                .replyToEmail(request.getReplyToEmail())
                .replyToName(request.getReplyToName())
                .subject(subject)
                .htmlBody(html)
                .status(EmailStatus.PENDING)
                .retryCount(0)
                .build();

        emailOutboxRepository.save(emailOutbox);
    }

    @Override
    public Page<EmailHistoryResponse> getEmailHistory(
            EmailStatus status,
            EmailType emailType,
            AggregateType aggregateType,
            String aggregateId,
            String toEmail,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        return emailOutboxRepository.searchEmailHistory(
                companyId, status, emailType, aggregateType, aggregateId, toEmail, startDate, endDate, pageable
        ).map(emailOutboxMapper::toHistoryResponse);
    }

    @Override
    public EmailHistoryDetailResponse getEmailHistoryById(String id) {
        EmailOutbox outbox = getOutboxForCurrentCompanyOrThrow(id);
        return emailOutboxMapper.toHistoryDetailResponse(outbox);
    }

    @Override
    @Transactional
    public void resend(String id) {
        EmailOutbox outbox = getOutboxForCurrentCompanyOrThrow(id);
        if (outbox.getStatus() != EmailStatus.FAILED) {
            throw new AppException(ErrorCode.EMAIL_CANNOT_RESEND);
        }
        outbox.setStatus(EmailStatus.PENDING);
        outbox.setRetryCount(0);
        outbox.setNextRetryAt(null);
        outbox.setFailedReason(null);
        emailOutboxRepository.save(outbox);
    }

    private EmailOutbox getOutboxForCurrentCompanyOrThrow(String id) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        return emailOutboxRepository.findByIdAndCompany_Id(id, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_OUTBOX_NOT_FOUND));
    }
}
