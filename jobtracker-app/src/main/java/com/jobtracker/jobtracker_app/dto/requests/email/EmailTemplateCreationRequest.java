package com.jobtracker.jobtracker_app.dto.requests.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EmailTemplateCreationRequest {

    @NotBlank(message = "{email_template.code.not_blank}")
    @Size(max = 100, message = "{email_template.code.size}")
    String code;

    @NotBlank(message = "{email_template.name.not_blank}")
    @Size(max = 255, message = "{email_template.name.size}")
    String name;

    @NotBlank(message = "{email_template.subject.not_blank}")
    @Size(max = 500, message = "{email_template.subject.size}")
    String subject;

    @NotBlank(message = "{email_template.html_content.not_blank}")
    String htmlContent;

    List<String> variables;

    @Size(max = 255, message = "{email_template.from_name.size}")
    String fromName;

    @NotNull(message = "{email_template.is_active.not_null}")
    Boolean isActive = true;
}
