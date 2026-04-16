package com.jobtracker.jobtracker_app.dto.requests.notification;

import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.enums.NotificationPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    @NotBlank(message = "{notification.user_id.not_blank}")
    String userId;

    @NotBlank(message = "{notification.company_id.not_blank}")
    String companyId;

    String jobId;

    String applicationId;

    @NotNull(message = "{notification.type.not_null}")
    NotificationType type;

    @NotNull(message = "{notification.priority.not_null}")
    NotificationPriority priority;

    @NotBlank(message = "{notification.title.not_blank}")
    @Size(max = 255, message = "{notification.title.size}")
    String title;

    @NotBlank(message = "{notification.message.not_blank}")
    String message;

    LocalDateTime scheduledAt;

    Map<String, Object> metadata;
}




