package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.dto.AuthLoginResult;
import com.kisalu.gestion.drh.common.dto.LoginAdminData;
import com.kisalu.gestion.drh.common.dto.TokenPair;
import com.kisalu.gestion.drh.common.security.JwtTokenProvider;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Module auth PHP : app/modules/auth/urls.php
 * Routes : POST loginAdmin, POST token/refresh, GET token/verify
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAuthService userAuthService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserAuthService userAuthService, JwtTokenProvider tokenProvider) {
        this.userAuthService = userAuthService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/loginAdmin")
    public ResponseEntity<ApiResponse<LoginAdminData>> loginAdmin(@RequestBody Map<String, String> body) {
        AuthLoginResult result = userAuthService.loginAdmin(body);
        return ApiResponse.of(result.code(), result.message(), result.data(), result.token());
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(HttpServletRequest request) {
        String headerToken = tokenProvider.extractTokenFromHeader(request.getHeader("Authorizations"));
        if (headerToken == null) {
            headerToken = tokenProvider.extractTokenFromHeader(request.getHeader("Authorization"));
        }
        TokenPair token = userAuthService.refreshToken(headerToken);
        return ApiResponse.of(200, "le token a été reproduit", null, token);
    }

    @GetMapping("/token/verify")
    @RequiresAuth
    public ResponseEntity<ApiResponse<Map<String, Object>>> verify() {
        return ApiResponse.of(200, "le token est encore valide", Map.of("valid", true));
    }
}
