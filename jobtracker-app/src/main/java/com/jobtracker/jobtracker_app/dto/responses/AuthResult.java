package com.jobtracker.jobtracker_app.dto.responses;

import com.jobtracker.jobtracker_app.dto.responses.user.UserInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResult {
    UserInfo user;
    TokenInfo tokenInfo;
}
