package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.dto.requests.email.ManualOfferRequest;
import com.jobtracker.jobtracker_app.dto.requests.email.SendEmailRequest;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailType;
import com.jobtracker.jobtracker_app.enums.ManualVariable;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import com.jobtracker.jobtracker_app.services.EmailService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {
    EmailOutboxService emailOutboxService;
    SecurityUtils securityUtils;

     // candidate_name, job_title, company_name, application_link
    @Override
    public void sendApplicationConfirmation(Application application) {
        EmailContext context = EmailContext.builder()
                .applicationId(application.getId())
                .companyId(application.getCompany().getId())
                .jobId(application.getJob().getId())
                .userId(securityUtils.getCurrentUser().getId())
                .build();

        sendEmail(
                EmailType.APPLICATION_CONFIRMATION,
                application.getCompany().getId(),
                AggregateType.APPLICATION,
                application.getId(),
                application.getCandidateEmail(),
                application.getCandidateName(),
                null,
                null,
                context
        );
    }


    // candidate_name, job_title, company_name, interview_time, interview_location, meeting_link, hr_name
    // custom_message
    @Override
    public void sendInterviewScheduled(Interview interview, String customMessage) {
        EmailContext context = EmailContext.builder()
                .applicationId(interview.getApplication().getId())
                .interviewId(interview.getId())
                .companyId(interview.getCompany().getId())
                .userId(securityUtils.getCurrentUser().getId())
                .manualValues(buildManualMap(customMessage))
                .build();

        sendEmail(
                EmailType.INTERVIEW_SCHEDULED,
                interview.getCompany().getId(),
                AggregateType.INTERVIEW,
                interview.getId(),
                interview.getApplication().getCandidateEmail(),
                interview.getApplication().getCandidateName(),
                getCurrentUserReplyToEmail(),
                getCurrentUserReplyToName(),
                context
        );
    }

     // candidate_name, job_title, company_name, interview_time, interview_location, meeting_link, hr_name
     // custom_message
    @Override
    public void sendInterviewRescheduled(Interview interview, String customMessage) {
        EmailContext context = EmailContext.builder()
                .applicationId(interview.getApplication().getId())
                .interviewId(interview.getId())
                .companyId(interview.getCompany().getId())
                .userId(securityUtils.getCurrentUser().getId())
                .manualValues(buildManualMap(customMessage))
                .build();

        sendEmail(
                EmailType.INTERVIEW_RESCHEDULED,
                interview.getCompany().getId(),
                AggregateType.INTERVIEW,
                interview.getId(),
                interview.getApplication().getCandidateEmail(),
                interview.getApplication().getCandidateName(),
                getCurrentUserReplyToEmail(),
                getCurrentUserReplyToName(),
                context
        );
    }

     // candidate_name, job_title, company_name, hr_name
     // custom_message
    @Override
    public void sendCandidateRejected(Application application, String customMessage) {
        EmailContext context = EmailContext.builder()
                .applicationId(application.getId())
                .companyId(application.getCompany().getId())
                .userId(securityUtils.getCurrentUser().getId())
                .manualValues(buildManualMap(customMessage))
                .build();

        sendEmail(
                EmailType.CANDIDATE_REJECTED,
                application.getCompany().getId(),
                AggregateType.APPLICATION,
                application.getId(),
                application.getCandidateEmail(),
                application.getCandidateName(),
                getCurrentUserReplyToEmail(),
                getCurrentUserReplyToName(),
                context
        );
    }

     // auto: candidate_name, job_title, company_name, hr_name
     // manual: custom_message
    @Override
    public void sendCandidateHired(Application application, String customMessage) {
        EmailContext context = EmailContext.builder()
                .applicationId(application.getId())
                .companyId(application.getCompany().getId())
                .userId(securityUtils.getCurrentUser().getId())
                .manualValues(buildManualMap(customMessage))
                .build();

        sendEmail(
                EmailType.CANDIDATE_HIRED,
                application.getCompany().getId(),
                AggregateType.APPLICATION,
                application.getId(),
                application.getCandidateEmail(),
                application.getCandidateName(),
                getCurrentUserReplyToEmail(),
                getCurrentUserReplyToName(),
                context
        );
    }

     // auto: candidate_name, job_title, company_name, hr_name
     // manual: offer_salary, offer_start_date, offer_expire_date, custom_message
    @Override
    public void sendManualOffer(Application application, ManualOfferRequest request) {
        Map<String, Object> manualMap = new HashMap<>();

        User user = securityUtils.getCurrentUser();

        manualMap.put(ManualVariable.OFFER_SALARY.getKey(), request.getOfferSalary());
        manualMap.put(ManualVariable.OFFER_START_DATE.getKey(), request.getOfferStartDate());
        manualMap.put(ManualVariable.OFFER_EXPIRE_DATE.getKey(), request.getOfferExpireDate());
        manualMap.put(ManualVariable.CUSTOM_MESSAGE.getKey(), request.getCustomMessage());

        EmailContext context = EmailContext.builder()
                .applicationId(application.getId())
                .companyId(application.getCompany().getId())
                .userId(user.getId())
                .manualValues(manualMap)
                .build();

        sendEmail(
                EmailType.MANUAL_OFFER,
                application.getCompany().getId(),
                AggregateType.APPLICATION,
                application.getId(),
                application.getCandidateEmail(),
                application.getCandidateName(),
                getCurrentUserReplyToEmail(),
                getCurrentUserReplyToName(),
                context
        );
    }

    private void sendEmail(
            EmailType type,
            String companyId,
            AggregateType aggregateType,
            String aggregateId,
            String recipientEmail,
            String recipientName,
            String replyToEmail,
            String replyToName,
            EmailContext context
    ) {
        SendEmailRequest request = SendEmailRequest.builder()
                .companyId(companyId)
                .templateCode(type)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .recipientEmail(recipientEmail)
                .recipientName(recipientName)
                .replyToEmail(replyToEmail)
                .replyToName(replyToName)
                .context(context)
                .build();

        emailOutboxService.createEmailOutbox(request);
    }

    private String getCurrentUserReplyToEmail() {
        return securityUtils.getCurrentUser().getEmail();
    }

    private String getCurrentUserReplyToName() {
        User user = securityUtils.getCurrentUser();
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }

    private Map<String, Object> buildManualMap(String customMessage) {
        if (customMessage == null || customMessage.isBlank()) {
            return null;
        }

        return Map.of(
                ManualVariable.CUSTOM_MESSAGE.getKey(),
                customMessage
        );
    }
}
