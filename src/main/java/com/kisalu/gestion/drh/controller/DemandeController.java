package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.common.web.AuthContext;
import com.kisalu.gestion.drh.service.DemandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Module demande PHP : app/modules/demande/urls.php
 * Table : rf_demande
 */
@RestController
@RequestMapping("/api/v1/demande")
@RequiresAuth
@Tag(name = "Demandes")
public class DemandeController {

    private final DemandeService demandeService;
    private final AuthContext authContext;

    public DemandeController(DemandeService demandeService, AuthContext authContext) {
        this.demandeService = demandeService;
        this.authContext = authContext;
    }

    @GetMapping({"", "/"})
    @Operation(summary = "Lister toutes les demandes", description = "Jointures agent demandeur/récepteur + service du demandeur.")
    public ResponseEntity<ApiResponse<Object>> listAll() {
        Map<String, Object> result = demandeService.listAll();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping({"", "/create"})
    @Operation(summary = "Créer une demande", description = """
            Body : `type_demande`, `message`, `id_agent_recepteur`.
            Le demandeur est déduit du token JWT (`user_rf` → op_agent).
            Statut initial : `en_attente`.
            """)
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody Map<String, String> body) {
        Map<String, Object> result = demandeService.create(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/by_id_recepteur")
    @Operation(summary = "Demandes par récepteur", description = "Paramètre : `id_recepteur`")
    public ResponseEntity<ApiResponse<Object>> byRecepteur(@RequestParam("id_recepteur") String idRecepteur) {
        Map<String, Object> result = demandeService.listByRecepteur(idRecepteur);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/by_id_demandeur")
    @Operation(summary = "Demandes par demandeur", description = "Paramètre : `id_demandeur`")
    public ResponseEntity<ApiResponse<Object>> byDemandeur(@RequestParam("id_demandeur") String idDemandeur) {
        Map<String, Object> result = demandeService.listByDemandeur(idDemandeur);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }
}
