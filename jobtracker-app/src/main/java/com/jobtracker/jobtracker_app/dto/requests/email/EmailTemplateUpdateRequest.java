package com.jobtracker.jobtracker_app.dto.requests.email;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailTemplateUpdateRequest {

    @Size(max = 500, message = "{email_template.subject.size}")
    String subject;

    String htmlContent;

    List<String> variables;

    @Size(max = 255, message = "{email_template.from_name.size}")
    String fromName;

    Boolean isActive;
}
