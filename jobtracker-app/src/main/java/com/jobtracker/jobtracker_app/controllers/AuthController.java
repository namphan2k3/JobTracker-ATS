package com.jobtracker.jobtracker_app.controllers;

import java.text.ParseException;
import java.time.Duration;

import com.jobtracker.jobtracker_app.dto.requests.auth.AcceptInviteRequest;
import com.jobtracker.jobtracker_app.dto.requests.auth.EmailVerifyRequest;
import com.jobtracker.jobtracker_app.dto.requests.auth.ForgotPasswordRequest;
import com.jobtracker.jobtracker_app.dto.requests.auth.RegisterRequest;
import com.jobtracker.jobtracker_app.dto.requests.auth.ResendEmailVerifyRequest;
import com.jobtracker.jobtracker_app.dto.requests.auth.ResetPasswordRequest;
import com.jobtracker.jobtracker_app.dto.responses.auth.AcceptInviteResponse;
import com.jobtracker.jobtracker_app.dto.responses.auth.AuthResult;
import com.jobtracker.jobtracker_app.dto.responses.company.CompanySelfSignupResponse;
import com.jobtracker.jobtracker_app.dto.responses.auth.EmailVerifyResponse;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.auth.AuthenticationRequest;
import com.jobtracker.jobtracker_app.dto.requests.auth.LogoutRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.AuthenticationResponse;
import com.jobtracker.jobtracker_app.services.AuthService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    AuthService authService;
    LocalizationUtils localizationUtils;

    @PostMapping("register")
    public ApiResponse<CompanySelfSignupResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.<CompanySelfSignupResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SELF_SIGNUP_SUCCESS))
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request,
                                              HttpServletResponse response)
            throws JOSEException {

        AuthResult authResult = authService.login(request);

        ResponseCookie responseCookie = ResponseCookie
                .from("refreshToken", authResult.getTokenInfo().getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(authResult.getTokenInfo().getRefreshMaxAge()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .user(authResult.getUser())
                .accessToken(authResult.getTokenInfo().getAccessToken())
                .expiresAt(authResult.getTokenInfo().getExpiresAt())
                .build();

        return ApiResponse.<AuthenticationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_LOGIN_SUCCESS))
                .data(authenticationResponse)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken( @CookieValue("refreshToken") String refreshToken,
                                                      HttpServletResponse response)
            throws ParseException, JOSEException {

        AuthResult authResult = authService.refreshToken(refreshToken);

        ResponseCookie responseCookie = ResponseCookie
                .from("refreshToken", authResult.getTokenInfo().getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .sameSite("Strict")
                .maxAge(Duration.ofSeconds(authResult.getTokenInfo().getRefreshMaxAge()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .user(authResult.getUser())
                .accessToken(authResult.getTokenInfo().getAccessToken())
                .expiresAt(authResult.getTokenInfo().getExpiresAt())
                .build();

        return ApiResponse.<AuthenticationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_REFRESH_SUCCESS))
                .data(authenticationResponse)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request,
                             @CookieValue(name = "refreshToken", required = false) String refreshToken,
                             HttpServletResponse response) throws ParseException, JOSEException {
        authService.logout(request, refreshToken);

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_LOGOUT_SUCCESS))
                .build();
    }

    @PostMapping("/verify-email")
    ApiResponse<EmailVerifyResponse> verifyEmail(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.<EmailVerifyResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_VERIFIED_SUCCESS))
                .data(authService.emailVerify(request))
                .build();
    }

    @PostMapping("/resend-verification")
    ApiResponse<Void> resendVerification(@RequestBody @Valid ResendEmailVerifyRequest request) {
        authService.resendEmailVerify(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.VERIFICATION_EMAIL_SENT))
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_RESET_EMAIL_SENT))
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_RESET_SUCCESS))
                .build();
    }

    @PostMapping("/accept-invite")
    ApiResponse<AcceptInviteResponse> acceptInvite(@RequestBody @Valid AcceptInviteRequest request) {
        return ApiResponse.<AcceptInviteResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INVITE_ACCEPTED_SUCCESS))
                .data(authService.acceptInvite(request))
                .build();
    }
}
