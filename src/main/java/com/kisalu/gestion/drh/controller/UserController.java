package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.dto.TokenPair;
import com.kisalu.gestion.drh.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Module user PHP : app/modules/user/urls.php
 * Route agent : POST /api/v1/user/agent (AgentTacView — login mobile agent).
 */
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController {

    private final UserAuthService userAuthService;

    public UserController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/agent")
    @Operation(summary = "Login agent mobile", description = """
            Application mobile agent (`user_type = AG`). Route publique.
            Jointure `op_agent` + personne + service + rôle. Vérifie `is_active = 1`.
            Body : `{ "login": "...", "pwd": "..." }`
            """)
    public ResponseEntity<ApiResponse<Object>> loginAgent(@RequestBody Map<String, String> body) {
        Map<String, Object> result = userAuthService.loginAgent(body);
        TokenPair token = (TokenPair) result.get("token");
        return ApiResponse.of(
                (int) result.get("code"),
                (String) result.get("message"),
                result.get("data"),
                token);
    }
}
