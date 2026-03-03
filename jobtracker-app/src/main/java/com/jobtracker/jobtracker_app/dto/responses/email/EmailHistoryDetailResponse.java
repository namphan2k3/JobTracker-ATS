package com.jobtracker.jobtracker_app.dto.responses.email;

import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.enums.EmailType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailHistoryDetailResponse {
    String id;
    EmailType emailType;
    AggregateType aggregateType;
    String aggregateId;
    String toEmail;
    String toName;
    String fromEmail;
    String fromName;
    String replyToEmail;
    String replyToName;
    String subject;
    String htmlBody;
    EmailStatus status;
    Integer retryCount;
    Integer maxRetries;
    LocalDateTime nextRetryAt;
    LocalDateTime sentAt;
    String failedReason;
    String providerMessageId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
