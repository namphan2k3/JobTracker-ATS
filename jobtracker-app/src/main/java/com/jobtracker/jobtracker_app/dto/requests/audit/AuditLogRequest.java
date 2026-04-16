package com.jobtracker.jobtracker_app.dto.requests.audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogRequest {
    String userId;

    @NotBlank(message = "{audit_log.entity_type.not_blank}")
    @Size(max = 100, message = "{audit_log.entity_type.size}")
    String entityType;

    @NotBlank(message = "{audit_log.entity_id.not_blank}")
    @Size(max = 36, message = "{audit_log.entity_id.size}")
    String entityId;

    @NotBlank(message = "{audit_log.action.not_blank}")
    @Size(max = 50, message = "{audit_log.action.size}")
    String action;

    String oldValues;

    String newValues;

    @Size(max = 45, message = "{audit_log.ip_address.size}")
    String ipAddress;

    String userAgent;
}




