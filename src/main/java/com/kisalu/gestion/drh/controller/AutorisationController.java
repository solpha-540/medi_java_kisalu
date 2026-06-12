package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.common.web.AuthContext;
import com.kisalu.gestion.drh.service.AutorisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Module autorisation PHP : app/modules/autorisation/urls.php */
@RestController
@RequestMapping("/api/v1/autorisation")
@RequiresAuth
public class AutorisationController {

    private final AutorisationService autorisationService;
    private final AuthContext authContext;

    public AutorisationController(AutorisationService autorisationService, AuthContext authContext) {
        this.autorisationService = autorisationService;
        this.authContext = authContext;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<Object>> list(@RequestParam(value = "id_op_agent", required = false) String idOpAgent) {
        Map<String, Object> result = autorisationService.list(idOpAgent, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/createAutorisation")
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody Map<String, String> body) {
        Map<String, Object> result = autorisationService.create(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }
}
