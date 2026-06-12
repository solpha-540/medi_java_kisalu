package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.model.RfFonction;
import com.kisalu.gestion.drh.model.RfServiceRh;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.RfFonctionRepository;
import com.kisalu.gestion.drh.repository.RfServiceRhRepository;
import com.kisalu.gestion.drh.repository.SysUsersRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD fonctions RH (FonctionViews PHP). Table : rf_fonction
 */
@org.springframework.stereotype.Service
public class RfFonctionService {


    private final RfFonctionRepository fonctionRepository;
    private final RfServiceRhRepository serviceRhRepository;
    private final SysUsersRepository usersRepository;
    private final RoleAuthorizationService roleAuthorizationService;

    public List<Map<String, Object>> findAll() {
        return fonctionRepository.findAll().stream().map(this::toMap).toList();
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body, AuthenticatedUser user) {
        String libelle = stringVal(body.get("libelle"));
        String idService = stringVal(body.get("id_service"));
        if (libelle.isBlank() || idService.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : libelle, id_service");
        }
        fonctionRepository.findByLibelle(libelle).ifPresent(f -> {
            throw new BusinessException(409, "Cette fonction existe déjà.");
        });

        RfServiceRh service = serviceRhRepository.findById(Integer.parseInt(idService))
                .orElseThrow(() -> new BusinessException(400, "Service introuvable"));
        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf()).orElse(null);

        RfFonction fonction = new RfFonction();
        fonction.setLibelle(libelle);
        fonction.setIdService(service);
        fonction.setIdUserCreated(creator);
        fonction.setCreatedAt(LocalDateTime.now());
        fonction.setLastUpdate(LocalDateTime.now());
        fonction = fonctionRepository.save(fonction);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 201);
        result.put("message", "Fonction créée avec succès");
        result.put("data", toMap(fonction));
        return result;
    }

    @Transactional
    public Map<String, Object> update(Integer id, Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        String libelle = stringVal(body.get("libelle"));
        if (libelle.isBlank()) {
            throw new BusinessException(400, "Le champ libelle est requis");
        }
        RfFonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Fonction introuvable"));
        fonction.setLibelle(libelle);
        fonction.setLastUpdate(LocalDateTime.now());
        fonctionRepository.save(fonction);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Fonction mise à jour avec succès");
        result.put("data", Map.of("libelle", libelle));
        return result;
    }

    @Transactional
    public Map<String, Object> delete(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        RfFonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Aucune fonction trouvée avec l'ID " + id));
        fonctionRepository.delete(fonction);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Fonction supprimée avec succès");
        result.put("data", Map.of("id", id));
        return result;
    }

    private Map<String, Object> toMap(RfFonction f) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", f.getId());
        map.put("libelle", f.getLibelle());
        map.put("id_service", f.getIdService() != null ? f.getIdService().getId() : null);
        map.put("created_at", f.getCreatedAt());
        map.put("last_update", f.getLastUpdate());
        return map;
    }

    private String stringVal(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public RfFonctionService(RfFonctionRepository fonctionRepository, RfServiceRhRepository serviceRhRepository, SysUsersRepository usersRepository, RoleAuthorizationService roleAuthorizationService) {
        this.fonctionRepository = fonctionRepository;
        this.serviceRhRepository = serviceRhRepository;
        this.usersRepository = usersRepository;
        this.roleAuthorizationService = roleAuthorizationService;
    }
}
