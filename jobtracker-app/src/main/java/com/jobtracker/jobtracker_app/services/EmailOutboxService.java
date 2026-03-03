package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.email.SendEmailRequest;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryDetailResponse;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryResponse;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.enums.EmailType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EmailOutboxService {
    void createEmailOutbox(SendEmailRequest request);

    Page<EmailHistoryResponse> getEmailHistory(
            EmailStatus status,
            EmailType emailType,
            AggregateType aggregateType,
            String aggregateId,
            String toEmail,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    EmailHistoryDetailResponse getEmailHistoryById(String id);

    void resend(String id);
}
