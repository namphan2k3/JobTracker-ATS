package com.jobtracker.jobtracker_app.dto.requests.email;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailTemplateSendTestRequest {

    /** Email to send test to. If null, use current user's email. */
    @Email(message = "{email_template.to_email.invalid}")
    String toEmail;
}
