package com.aegisflow.api.identity.controller;

import com.aegisflow.api.common.api.ApiResponse;
import com.aegisflow.api.common.security.SecurityUtils;
import com.aegisflow.api.identity.dto.request.LoginRequest;
import com.aegisflow.api.identity.dto.request.LogoutRequest;
import com.aegisflow.api.identity.dto.request.RefreshTokenRequest;
import com.aegisflow.api.identity.dto.request.RegisterUserRequest;
import com.aegisflow.api.identity.dto.response.AuthTokenResponse;
import com.aegisflow.api.identity.dto.response.LogoutResponse;
import com.aegisflow.api.identity.dto.response.UserRegistrationResponse;
import com.aegisflow.api.identity.service.AuthRequestContext;
import com.aegisflow.api.identity.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<UserRegistrationResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ApiResponse.success("Registration completed", authenticationService.register(request));
    }

    @PostMapping("/login")
    ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return ApiResponse.success("Authentication successful", authenticationService.login(request, context(servletRequest)));
    }

    @PostMapping("/refresh")
    ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest servletRequest) {
        return ApiResponse.success("Token refreshed", authenticationService.refresh(request.refreshToken(), context(servletRequest)));
    }

    @PostMapping("/logout")
    ApiResponse<LogoutResponse> logout(@Valid @RequestBody LogoutRequest request) {
        return ApiResponse.success("Logout completed", authenticationService.logout(request.refreshToken()));
    }

    @PostMapping("/logout-all")
    ApiResponse<Map<String, Boolean>> logoutAll() {
        authenticationService.logoutAll(SecurityUtils.currentUserId());
        return ApiResponse.success("All sessions revoked", Map.of("revoked", true));
    }

    private AuthRequestContext context(HttpServletRequest request) {
        return new AuthRequestContext(request.getRemoteAddr(), request.getHeader(HttpHeaders.USER_AGENT));
    }
}
