package com.jobtracker.jobtracker_app.services;

import java.text.ParseException;

import com.jobtracker.jobtracker_app.dto.requests.*;
import com.jobtracker.jobtracker_app.dto.responses.AcceptInviteResponse;
import com.jobtracker.jobtracker_app.dto.responses.AuthResult;
import com.jobtracker.jobtracker_app.dto.responses.CompanySelfSignupResponse;
import com.jobtracker.jobtracker_app.dto.responses.EmailVerifyResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.AuthenticationResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthService {
    CompanySelfSignupResponse register(RegisterRequest request);

    AuthResult login(AuthenticationRequest request) throws JOSEException;

    EmailVerifyResponse emailVerify(EmailVerifyRequest request);

    void resendEmailVerify(ResendEmailVerifyRequest request);

    AuthResult refreshToken(String refreshToken) throws ParseException, JOSEException;

    void logout(LogoutRequest request, String refreshTokenFromCookie) throws ParseException, JOSEException;

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    AcceptInviteResponse acceptInvite(AcceptInviteRequest request);
}
