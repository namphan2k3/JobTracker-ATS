package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @NotBlank(message = "{user.email.not_blank}")
    @Email(message = "{user.email.invalid}")
    @Size(max = 255, message = "{user.email.size}")
    String email;

    @NotBlank(message = "{user.first_name.not_blank}")
    @Size(max = 100, message = "{user.first_name.size}")
    String firstName;

    @NotBlank(message = "{user.last_name.not_blank}")
    @Size(max = 100, message = "{user.last_name.size}")
    String lastName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "{user.phone.pattern}")
    @Size(max = 20, message = "{user.phone.size}")
    String phone;
}
