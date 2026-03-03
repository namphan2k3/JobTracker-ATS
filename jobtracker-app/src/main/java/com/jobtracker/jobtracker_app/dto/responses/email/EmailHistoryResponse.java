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
public class EmailHistoryResponse {
    String id;
    EmailType emailType;
    AggregateType aggregateType;
    String aggregateId;
    String toEmail;
    String toName;
    String subject;
    EmailStatus status;
    Integer retryCount;
    LocalDateTime sentAt;
    LocalDateTime createdAt;
}
