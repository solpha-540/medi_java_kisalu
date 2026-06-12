package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.model.RfGrade;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.RfGradeRepository;
import com.kisalu.gestion.drh.repository.SysUsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** CRUD grades (GradeViews). Table : rf_grade */
@Service
public class GradeService {


    private final RfGradeRepository gradeRepository;
    private final SysUsersRepository usersRepository;
    private final RoleAuthorizationService roleAuthorizationService;

    public List<Map<String, Object>> findAll() {
        return gradeRepository.findAll().stream().map(this::toMap).toList();
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body, AuthenticatedUser user) {
        String libelle = stringVal(body.get("libelle"));
        if (libelle.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : libelle");
        }
        gradeRepository.findByLibelle(libelle).ifPresent(g -> {
            throw new BusinessException(409, "Ce grade existe déjà.");
        });

        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf()).orElse(null);
        RfGrade grade = new RfGrade();
        grade.setLibelle(libelle);
        grade.setSalaire(body.get("salaire") != null ? new BigDecimal(body.get("salaire").toString()) : BigDecimal.ZERO);
        grade.setCodeClassification(stringVal(body.get("code_classification")));
        grade.setIdUserCreated(creator);
        grade.setCreatedAt(LocalDateTime.now());
        grade.setLastUpdate(LocalDateTime.now());
        grade = gradeRepository.save(grade);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 201);
        result.put("message", "Grade créé avec succès");
        result.put("data", toMap(grade));
        return result;
    }

    @Transactional
    public Map<String, Object> update(Integer id, Map<String, Object> body, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        String libelle = stringVal(body.get("libelle"));
        if (libelle.isBlank()) {
            throw new BusinessException(400, "Le champ libelle est requis");
        }
        RfGrade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Grade introuvable"));
        grade.setLibelle(libelle);
        if (body.get("salaire") != null) {
            grade.setSalaire(new BigDecimal(body.get("salaire").toString()));
        }
        grade.setLastUpdate(LocalDateTime.now());
        gradeRepository.save(grade);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Grade mis à jour avec succès");
        result.put("data", Map.of("libelle", libelle));
        return result;
    }

    @Transactional
    public Map<String, Object> delete(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");
        RfGrade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Aucun grade trouvé avec l'ID " + id));
        gradeRepository.delete(grade);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Grade supprimé avec succès");
        result.put("data", Map.of("id", id));
        return result;
    }

    private Map<String, Object> toMap(RfGrade g) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", g.getId());
        map.put("libelle", g.getLibelle());
        map.put("salaire", g.getSalaire());
        map.put("code_classification", g.getCodeClassification());
        map.put("created_at", g.getCreatedAt());
        map.put("last_update", g.getLastUpdate());
        return map;
    }

    private String stringVal(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public GradeService(RfGradeRepository gradeRepository, SysUsersRepository usersRepository, RoleAuthorizationService roleAuthorizationService) {
        this.gradeRepository = gradeRepository;
        this.usersRepository = usersRepository;
        this.roleAuthorizationService = roleAuthorizationService;
    }
}
