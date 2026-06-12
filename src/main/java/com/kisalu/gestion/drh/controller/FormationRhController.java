package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.common.web.AuthContext;
import com.kisalu.gestion.drh.service.FormationRhService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Module formation PHP : app/modules/formation/urls.php */
@RestController
@RequestMapping("/api/v1/formation")
@RequiresAuth
public class FormationRhController {

    private final FormationRhService formationRhService;
    private final AuthContext authContext;

    public FormationRhController(FormationRhService formationRhService, AuthContext authContext) {
        this.formationRhService = formationRhService;
        this.authContext = authContext;
    }

    @PostMapping({"", "/create"})
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = formationRhService.create(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/all", ""})
    public ResponseEntity<ApiResponse<Object>> listAll() {
        Map<String, Object> result = formationRhService.listAll();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/by_id")
    public ResponseEntity<ApiResponse<Object>> getById(@RequestParam Integer id) {
        Map<String, Object> result = formationRhService.getById(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/agent_id")
    public ResponseEntity<ApiResponse<Object>> getByAgent(@RequestParam("agent_id") Integer agentId) {
        Map<String, Object> result = formationRhService.getByAgent(agentId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(
            @RequestParam Integer id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = formationRhService.update(id, body);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        return ApiResponse.of(501, "delete formation - à implémenter", null);
    }
}
