package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.common.web.AuthContext;
import com.kisalu.gestion.drh.service.DirectionService;
import com.kisalu.gestion.drh.service.RfFonctionService;
import com.kisalu.gestion.drh.service.GradeService;
import com.kisalu.gestion.drh.service.ServiceRhService;
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

import java.util.List;
import java.util.Map;

/**
 * Module affectationRh PHP : app/modules/affectationRh/
 * Organisation RH : directions, services, fonctions, grades, affectations hierarchiques.
 */
@RestController
@RequestMapping("/api/v1/affectationRh")
@RequiresAuth
public class AffectationRhController {


    private final DirectionService directionService;
    private final ServiceRhService serviceRhService;
    private final RfFonctionService fonctionService;
    private final GradeService gradeService;
    private final AuthContext authContext;

    // --- Direction ---
    @GetMapping({"/direction", "/direction/{segment}"})
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listDirections(@PathVariable(required = false) String segment) {
        List<Map<String, Object>> data = directionService.findAll();
        if (data.isEmpty()) {
            return ApiResponse.of(404, "Aucune direction trouvée", List.of());
        }
        return ApiResponse.of(200, "Voici la liste des directions", data);
    }

    @PostMapping({"/direction", "/direction/{segment}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> createDirection(
            @RequestBody Map<String, Object> body,
            @PathVariable(required = false) String segment) {
        Map<String, Object> result = directionService.create(body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @PutMapping("/direction/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateDirection(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = directionService.update(id, body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @DeleteMapping("/directionDelete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteDirection(@RequestParam Integer id) {
        Map<String, Object> result = directionService.delete(id, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    // --- Service RH ---
    @GetMapping({"/service_rh", "/service_rh/{segment}"})
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listServices(@PathVariable(required = false) String segment) {
        List<Map<String, Object>> data = serviceRhService.findAll();
        if (data.isEmpty()) {
            return ApiResponse.of(404, "Aucun service trouvé", List.of());
        }
        return ApiResponse.of(200, "Voici la liste des services", data);
    }

    @PostMapping({"/service_rh", "/service_rh/{segment}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> createService(
            @RequestBody Map<String, Object> body,
            @PathVariable(required = false) String segment) {
        Map<String, Object> result = serviceRhService.create(body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @PutMapping({"/service_rh", "/service_rh/{segment}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateService(
            @RequestParam Integer id,
            @RequestBody Map<String, Object> body,
            @PathVariable(required = false) String segment) {
        Map<String, Object> result = serviceRhService.update(id, body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @DeleteMapping("/service_rhDelete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteService(@RequestParam Integer id) {
        Map<String, Object> result = serviceRhService.delete(id, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    // --- Affectation hiérarchique ---
    @PostMapping("/direction/affecter-directeur")
    public ResponseEntity<ApiResponse<Map<String, Object>>> affecterDirecteur(@RequestBody Map<String, Object> body) {
        body.put("type", "direction");
        Map<String, Object> result = serviceRhService.affecterResponsable("direction", body);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @PostMapping("/service/affecter-chef")
    public ResponseEntity<ApiResponse<Map<String, Object>>> affecterChef(@RequestBody Map<String, Object> body) {
        body.put("type", "service");
        Map<String, Object> result = serviceRhService.affecterResponsable("service", body);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    // --- Fonction ---
    @GetMapping({"/fonction", "/fonction/{segment}"})
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listFonctions(@PathVariable(required = false) String segment) {
        List<Map<String, Object>> data = fonctionService.findAll();
        if (data.isEmpty()) {
            return ApiResponse.of(404, "Aucune fonction trouvée", List.of());
        }
        return ApiResponse.of(200, "Voici la liste des fonctions", data);
    }

    @PostMapping({"/fonction", "/fonction/{segment}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> createFonction(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = fonctionService.create(body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @PutMapping({"/fonction", "/fonction/{segment}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateFonction(
            @RequestParam Integer id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = fonctionService.update(id, body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @DeleteMapping("/fonctionDelete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteFonction(@RequestParam Integer id) {
        Map<String, Object> result = fonctionService.delete(id, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    // --- Grade ---
    @GetMapping({"/grade", "/grade/{segment}"})
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listGrades(@PathVariable(required = false) String segment) {
        List<Map<String, Object>> data = gradeService.findAll();
        if (data.isEmpty()) {
            return ApiResponse.of(404, "Aucun grade trouvé", List.of());
        }
        return ApiResponse.of(200, "Voici la liste des grades", data);
    }

    @PostMapping({"/grade", "/grade/{segment}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> createGrade(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = gradeService.create(body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @PutMapping("/grade/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateGrade(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = gradeService.update(id, body, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    @DeleteMapping("/gradeDelete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteGrade(@RequestParam Integer id) {
        Map<String, Object> result = gradeService.delete(id, currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), (Map<String, Object>) result.get("data"));
    }

    private AuthenticatedUser currentUser() {
        AuthenticatedUser user = authContext.currentUser();
        if (user == null) {
            throw new BusinessException(401, "Le token n'est pas valide");
        }
        return user;
    }

    public AffectationRhController(DirectionService directionService, ServiceRhService serviceRhService, RfFonctionService fonctionService, GradeService gradeService, AuthContext authContext) {
        this.directionService = directionService;
        this.serviceRhService = serviceRhService;
        this.fonctionService = fonctionService;
        this.gradeService = gradeService;
        this.authContext = authContext;
    }
}
