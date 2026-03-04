package com.jobtracker.jobtracker_app.dto.responses.common;

import com.jobtracker.jobtracker_app.dto.responses.TokenInfo;
import com.jobtracker.jobtracker_app.dto.responses.user.UserInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    UserInfo user;
    String accessToken;
    Date expiresAt;
}
