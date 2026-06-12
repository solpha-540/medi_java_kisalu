package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.model.RfDirection;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.RfDirectionRepository;
import com.kisalu.gestion.drh.repository.SysUsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** CRUD directions RH (DirectionViews). Table : rf_direction */
@Service
public class DirectionService {


    private final RfDirectionRepository directionRepository;
    private final SysUsersRepository usersRepository;
    private final RoleAuthorizationService roleAuthorizationService;

    public List<Map<String, Object>> findAll() {
        return directionRepository.findAll().stream().map(this::toMap).toList();
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body, AuthenticatedUser user) {
        String libele = stringVal(body.get("libele"));
        if (libele.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : libele");
        }
        directionRepository.findByLibele(libele).ifPresent(d -> {
            throw new BusinessException(409, "Cette direction existe déjà");
        });

        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf())
                .orElse(null);

        RfDirection direction = new RfDirection();
        direction.setLibele(libele);
        direction.setCreatedAt(LocalDateTime.now());
        direction.setLastUpdate(LocalDateTime.now());
        direction.setIdUserCreatedAt(creator);
        direction = directionRepository.save(direction);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 201);
        result.put("message", "Direction créée avec succès");
        result.put("data", toMap(direction));
        return result;
    }

    @Transactional
    public Map<String, Object> update(Integer id, Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        String libele = stringVal(body.get("libele"));
        if (libele.isBlank()) {
            throw new BusinessException(400, "Le champ libele est requis");
        }

        RfDirection direction = directionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Direction introuvable."));
        direction.setLibele(libele);
        direction.setLastUpdate(LocalDateTime.now());
        directionRepository.save(direction);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Direction mise à jour avec succès.");
        result.put("data", Map.of("libele", libele));
        return result;
    }

    @Transactional
    public Map<String, Object> delete(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        RfDirection direction = directionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Aucune direction trouvée avec l'ID " + id));
        directionRepository.delete(direction);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Direction supprimée avec succès");
        result.put("data", Map.of("id", id));
        return result;
    }

    private Map<String, Object> toMap(RfDirection d) {
        Map<String, Object> map = new HashMap<>();
        map.put("Id", d.getId());
        map.put("libele", d.getLibele());
        map.put("created_at", d.getCreatedAt());
        map.put("last_update", d.getLastUpdate());
        map.put("id_directeur", d.getIdDirecteur());
        map.put("code_direction", d.getCodeDirection());
        return map;
    }

    private String stringVal(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public DirectionService(RfDirectionRepository directionRepository, SysUsersRepository usersRepository, RoleAuthorizationService roleAuthorizationService) {
        this.directionRepository = directionRepository;
        this.usersRepository = usersRepository;
        this.roleAuthorizationService = roleAuthorizationService;
    }
}
