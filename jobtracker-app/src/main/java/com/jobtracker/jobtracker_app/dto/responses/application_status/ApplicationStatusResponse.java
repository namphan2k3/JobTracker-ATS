package com.jobtracker.jobtracker_app.dto.responses.application_status;

import com.jobtracker.jobtracker_app.enums.StatusType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationStatusResponse {
    String id;
    String companyId;
    String name;
    String displayName;
    String description;
    String color;
    StatusType statusType;
    Integer sortOrder;
     Boolean autoSendEmail;
     Boolean askBeforeSend;
    Boolean isTerminal;
    Boolean isDefault;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}

