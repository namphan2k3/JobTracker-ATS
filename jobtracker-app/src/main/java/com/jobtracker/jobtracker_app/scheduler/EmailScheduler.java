package com.jobtracker.jobtracker_app.scheduler;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailRequest;
import com.jobtracker.jobtracker_app.dto.requests.email.Recipient;
import com.jobtracker.jobtracker_app.dto.requests.email.ReplyTo;
import com.jobtracker.jobtracker_app.dto.requests.email.Sender;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailResponse;
import com.jobtracker.jobtracker_app.entities.EmailOutbox;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.repositories.EmailOutboxRepository;
import com.jobtracker.jobtracker_app.repositories.httpclient.EmailClient;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailScheduler {
    EmailOutboxService emailOutboxService;
    EmailOutboxRepository emailOutboxRepository;
    EmailClient emailClient;

    @Value("${brevo.api-key}")
    @NonFinal
    String apiKey;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processEmailQueue() {
        Pageable pageable = PageRequest.of(0, 50);

        List<EmailOutbox> emails =
                emailOutboxRepository.findPendingEmails(LocalDateTime.now(), pageable);

        for (EmailOutbox email : emails) {

            try {
                EmailResponse response = sendFromOutbox(email);

                email.setStatus(EmailStatus.SENT);
                email.setSentAt(LocalDateTime.now());
                email.setProviderMessageId(response.getMessageId());

            } catch (Exception ex) {
                int retry = email.getRetryCount() + 1;

                if (retry >= email.getMaxRetries()) {
                    email.setStatus(EmailStatus.FAILED);
                    email.setFailedReason(ex.getLocalizedMessage());
                } else {
                    email.setRetryCount(retry);
                    email.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
                }
            }
        }
    }

    public EmailResponse sendFromOutbox(EmailOutbox email) {

        Sender sender = Sender.builder()
                .email(email.getFromEmail())
                .name(email.getFromName())
                .build();

        Recipient recipient = Recipient.builder()
                .email(email.getToEmail())
                .name(email.getToName())
                .build();

        ReplyTo replyTo = ReplyTo.builder()
                .email(email.getReplyToEmail())
                .name(email.getReplyToName())
                .build();

        EmailRequest emailRequest = EmailRequest.builder()
                .sender(sender)
                .to(List.of(recipient))
                .replyTo(replyTo)
                .subject(email.getSubject())
                .htmlContent(email.getHtmlBody())
                .build();

        return emailClient.sendEmail(apiKey, emailRequest);
    }
}
