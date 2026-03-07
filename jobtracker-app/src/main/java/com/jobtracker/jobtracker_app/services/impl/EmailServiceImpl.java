package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.dto.requests.email.ManualOfferRequest;
import com.jobtracker.jobtracker_app.dto.requests.email.SendEmailRequest;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.EmailVerificationToken;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.entities.PasswordResetToken;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.entities.UserInvitation;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailType;
import com.jobtracker.jobtracker_app.enums.ManualVariable;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import com.jobtracker.jobtracker_app.services.EmailService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {
    EmailOutboxService emailOutboxService;
    SecurityUtils securityUtils;

    @Value("${brevo.email-system}")
    @NonFinal
    String systemEmail;

    @Value("${brevo.email-system-name}")
    @NonFinal
    String systemName;

     // auto: candidate_name, job_title, company_name, application_link
    @Override
    public void sendApplicationConfirmation(Application application) {
        EmailContext context = EmailContext.builder()
                .applicationId(application.getId())
                .companyId(application.getCompany().getId())
                .jobId(application.getJob().getId())
                .build();

        sendEmail(
                EmailType.APPLICATION_CONFIRMATION,
                application.getCompany().getId(),
                AggregateType.APPLICATION,
                application.getId(),
                application.getCandidateEmail(),
                application.getCandidateName(),
                systemEmail,
                systemName,
                context
        );
    }


    // auto: candidate_name, job_title, company_name, interview_time, interview_location, meeting_link, hr_name
    // manual: custom_message
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

     // auto: candidate_name, job_title, company_name, interview_time, interview_location, meeting_link, hr_name
     // manual: custom_message
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

     // auto: candidate_name, job_title, company_name, hr_name
     // manual: custom_message
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

    // auto: company_name, user_email, user_first_name, user_last_name, user_name, invite_link
    @Override
    public void sendUserInvite(User user, UserInvitation invitation) {
        EmailContext context = EmailContext.builder()
                .userId(user.getId())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .inviteToken(invitation.getToken())
                .build();

        sendEmail(
                EmailType.USER_INVITE,
                user.getCompany().getId(),
                AggregateType.USER,
                user.getId(),
                user.getEmail(),
                buildUserName(user),
                systemEmail,
                systemName,
                context
        );
    }

    // auto: company_name, user_email, user_first_name, user_last_name, user_name, invite_link
    @Override
    public void sendUserInviteResend(User user, UserInvitation invitation) {
        EmailContext context = EmailContext.builder()
                .userId(user.getId())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .inviteToken(invitation.getToken())
                .build();

        sendEmail(
                EmailType.USER_INVITE_RESEND,
                user.getCompany().getId(),
                AggregateType.USER,
                user.getId(),
                user.getEmail(),
                buildUserName(user),
                systemEmail,
                systemName,
                context
        );
    }

    // auto: company_name, user_email, user_first_name, user_last_name, user_name, verification_link
    @Override
    public void sendEmailVerification(User user, EmailVerificationToken token) {
        EmailContext context = EmailContext.builder()
                .userId(user.getId())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .verificationToken(token.getToken())
                .build();

        sendEmail(
                EmailType.EMAIL_VERIFICATION,
                user.getCompany().getId(),
                AggregateType.USER,
                user.getId(),
                user.getEmail(),
                buildUserName(user),
                systemEmail,
                systemName,
                context
        );
    }

    // auto: company_name, user_email, user_first_name, user_last_name, user_name, verification_link
    @Override
    public void sendEmailVerificationResend(User user, EmailVerificationToken token) {
        EmailContext context = EmailContext.builder()
                .userId(user.getId())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .verificationToken(token.getToken())
                .build();

        sendEmail(
                EmailType.EMAIL_VERIFICATION_RESEND,
                user.getCompany().getId(),
                AggregateType.USER,
                user.getId(),
                user.getEmail(),
                buildUserName(user),
                systemEmail,
                systemName,
                context
        );
    }

    // auto: company_name, user_email, user_first_name, user_last_name, user_name, reset_link
    @Override
    public void sendPasswordReset(User user, PasswordResetToken token) {
        EmailContext context = EmailContext.builder()
                .userId(user.getId())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .resetToken(token.getToken())
                .build();

        sendEmail(
                EmailType.PASSWORD_RESET,
                user.getCompany().getId(),
                AggregateType.USER,
                user.getId(),
                user.getEmail(),
                buildUserName(user),
                systemEmail,
                systemName,
                context
        );
    }

    private static String buildUserName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        return (first + " " + last).trim();
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
        return Map.of(
                ManualVariable.CUSTOM_MESSAGE.getKey(),
                customMessage == null ? "" : customMessage
        );
    }
}
