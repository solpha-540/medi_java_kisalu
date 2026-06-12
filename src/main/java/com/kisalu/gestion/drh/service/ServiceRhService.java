package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.model.RfDirection;
import com.kisalu.gestion.drh.model.RfServiceRh;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.RfDirectionRepository;
import com.kisalu.gestion.drh.repository.RfServiceRhRepository;
import com.kisalu.gestion.drh.repository.SysUsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** CRUD services RH + affectation chef/directeur (ServiceRhViews). Table : rf_service_rh */
@Service
public class ServiceRhService {


    private final RfServiceRhRepository serviceRhRepository;
    private final RfDirectionRepository directionRepository;
    private final SysUsersRepository usersRepository;
    private final RoleAuthorizationService roleAuthorizationService;

    public List<Map<String, Object>> findAll() {
        return serviceRhRepository.findAll().stream().map(this::toMap).toList();
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body, AuthenticatedUser user) {
        String libelle = stringVal(body.get("libelle"));
        String idDirection = stringVal(body.get("id_direction"));
        if (libelle.isBlank() || idDirection.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : id_direction, libelle");
        }
        serviceRhRepository.findByLibelle(libelle).ifPresent(s -> {
            throw new BusinessException(409, "Ce service existe déjà.");
        });

        RfDirection direction = directionRepository.findById(Integer.parseInt(idDirection))
                .orElseThrow(() -> new BusinessException(400, "Direction introuvable"));
        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf()).orElse(null);

        RfServiceRh service = new RfServiceRh();
        service.setLibelle(libelle);
        service.setIdDirection(direction);
        service.setIdUserCreated(creator);
        service.setCreatedAt(LocalDateTime.now());
        service.setLastUpdate(LocalDateTime.now());
        service = serviceRhRepository.save(service);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 201);
        result.put("message", "Service créé avec succès");
        result.put("data", toMap(service));
        return result;
    }

    @Transactional
    public Map<String, Object> update(Integer id, Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        String libelle = stringVal(body.get("libelle"));
        if (libelle.isBlank()) {
            throw new BusinessException(400, "Le champ libelle est requis");
        }
        RfServiceRh service = serviceRhRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Service introuvable"));
        service.setLibelle(libelle);
        service.setLastUpdate(LocalDateTime.now());
        serviceRhRepository.save(service);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Service mis à jour avec succès");
        result.put("data", Map.of("libelle", libelle));
        return result;
    }

    @Transactional
    public Map<String, Object> delete(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        RfServiceRh service = serviceRhRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Aucun service trouvé avec l'ID " + id));
        serviceRhRepository.delete(service);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Service supprimé avec succès");
        result.put("data", Map.of("id", id));
        return result;
    }

    @Transactional
    public Map<String, Object> affecterResponsable(String type, Map<String, Object> body) {
        String idAgent = stringVal(body.get("id_agent"));
        if (idAgent.isBlank()) {
            throw new BusinessException(400, "La clé id_agent est manquante.");
        }
        int agentId = Integer.parseInt(idAgent);

        if ("direction".equals(type)) {
            String idDirection = stringVal(body.get("id_direction"));
            if (idDirection.isBlank()) {
                throw new BusinessException(400, "La clé id_direction est manquante.");
            }
            RfDirection direction = directionRepository.findById(Integer.parseInt(idDirection))
                    .orElseThrow(() -> new BusinessException(404, "Direction introuvable"));
            direction.setIdDirecteur(agentId);
            direction.setLastUpdate(LocalDateTime.now());
            directionRepository.save(direction);
            return Map.of("code", 200, "message", "Directeur affecté avec succès", "data", Map.of("id_direction", direction.getId()));
        }

        if ("service".equals(type)) {
            String idService = stringVal(body.get("id_service"));
            if (idService.isBlank()) {
                throw new BusinessException(400, "La clé id_service est manquante.");
            }
            RfServiceRh service = serviceRhRepository.findById(Integer.parseInt(idService))
                    .orElseThrow(() -> new BusinessException(404, "Service introuvable"));
            service.setIdChefservice(agentId);
            service.setLastUpdate(LocalDateTime.now());
            serviceRhRepository.save(service);
            return Map.of("code", 200, "message", "Chef de service affecté avec succès", "data", Map.of("id_service", service.getId()));
        }

        throw new BusinessException(400, "Le type d'affectation est requis.");
    }

    private Map<String, Object> toMap(RfServiceRh s) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("libelle", s.getLibelle());
        map.put("id_direction", s.getIdDirection() != null ? s.getIdDirection().getId() : null);
        map.put("id_chefService", s.getIdChefservice());
        map.put("created_at", s.getCreatedAt());
        map.put("last_update", s.getLastUpdate());
        return map;
    }

    private String stringVal(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public ServiceRhService(RfServiceRhRepository serviceRhRepository, RfDirectionRepository directionRepository, SysUsersRepository usersRepository, RoleAuthorizationService roleAuthorizationService) {
        this.serviceRhRepository = serviceRhRepository;
        this.directionRepository = directionRepository;
        this.usersRepository = usersRepository;
        this.roleAuthorizationService = roleAuthorizationService;
    }
}
