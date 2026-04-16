package com.jobtracker.jobtracker_app.dto.requests.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptInviteRequest {
    @NotBlank(message = "{auth.token.not_blank}")
    String token;

    @NotBlank(message = "{user.password.not_blank}")
    String password;
}
