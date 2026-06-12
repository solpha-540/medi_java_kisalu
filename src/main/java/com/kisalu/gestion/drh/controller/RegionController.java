package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.common.web.AuthContext;
import com.kisalu.gestion.drh.service.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Module region PHP : app/modules/region/urls.php */
@RestController
@RequestMapping("/api/v1/region")
@RequiresAuth
public class RegionController {

    private final RegionService regionService;
    private final AuthContext authContext;

    public RegionController(RegionService regionService, AuthContext authContext) {
        this.regionService = regionService;
        this.authContext = authContext;
    }

    @GetMapping({"/province/get", "/province/{action}"})
    public ResponseEntity<ApiResponse<Object>> listProvinces(@PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.listProvinces();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping({"/province/create", "/province/{action}"})
    public ResponseEntity<ApiResponse<Object>> createProvince(
            @RequestBody Map<String, String> body,
            @PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.createProvince(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ville/get", "/ville/{action}"})
    public ResponseEntity<ApiResponse<Object>> listVilles(
            @RequestParam(required = false) Integer id,
            @PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.listVilles(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping({"/ville/create", "/ville/{action}"})
    public ResponseEntity<ApiResponse<Object>> createVille(
            @RequestBody Map<String, String> body,
            @PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.createVille(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/commune/get", "/commune/{action}"})
    public ResponseEntity<ApiResponse<Object>> listCommunes(
            @RequestParam(required = false) Integer id,
            @PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.listCommunes(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping({"/commune/create", "/commune/{action}"})
    public ResponseEntity<ApiResponse<Object>> createCommune(
            @RequestBody Map<String, String> body,
            @PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.createCommune(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/adresse/get", "/adresse/{action}"})
    public ResponseEntity<ApiResponse<Object>> listAddresses(@PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.listAddresses();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping({"/adresse/create", "/adresse/{action}"})
    public ResponseEntity<ApiResponse<Object>> createAddress(
            @RequestBody Map<String, String> body,
            @PathVariable(required = false) String action) {
        Map<String, Object> result = regionService.createAddress(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }
}
