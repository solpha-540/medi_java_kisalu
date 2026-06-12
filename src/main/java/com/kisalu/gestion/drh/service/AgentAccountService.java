package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.common.util.AgentFormValidator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Comptes agents (CompteAgentView, CompteUsersView, CompteActivationView PHP). */
@Service
public class AgentAccountService {

    private static final String ALL_ACCOUNTS_SQL = """
            SELECT CONCAT(p.nom, ' ', p.post_nom, ' ', p.prenom) AS nomCompletAgent,
                   u.login, r.libelle AS roleLibelle, u.created_at AS userCreatedAt,
                   u.id_generate, u.user_type, u.is_active, u.last_update AS userLastUpdate,
                   a.id AS agentId
            FROM sys_users u
            LEFT JOIN sys_user_roles ur ON ur.id_users = u.id
            LEFT JOIN sys_roles r ON r.id = ur.id_role
            LEFT JOIN op_agent a ON a.code_generate_tac = u.id_generate
            LEFT JOIN rf_personnes p ON p.id = a.personne_id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UpdateHistoryService updateHistoryService;

    public AgentAccountService(
            NamedParameterJdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            UpdateHistoryService updateHistoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.updateHistoryService = updateHistoryService;
    }

    @Transactional
    public Map<String, Object> createCompteAgent(Map<String, String> body) {
        for (String field : List.of("pwd", "pwd_repeat", "telephone", "Agent_id", "id_user_created", "role")) {
            if (body.get(field) == null || body.get(field).isBlank()) {
                throw new BusinessException(400, "Le champ requis \"" + field + "\" est vide ou manquant.");
            }
        }

        String pwd = body.get("pwd");
        if (!pwd.equals(body.get("pwd_repeat"))) {
            throw new BusinessException(400, "les deux mots de passe ne matchent pas");
        }

        String telephone = AgentFormValidator.normalizePhone(body.get("telephone"));
        String agentCode = findAgentCode(Integer.parseInt(body.get("Agent_id")));

        if (jdbcTemplate.queryForList("SELECT id FROM sys_users WHERE login = :login", Map.of("login", telephone)).size() > 0) {
            throw new BusinessException(400, "le numéro " + telephone + " existe déjà dans notre système");
        }

        Map<String, Object> userParams = new HashMap<>();
        userParams.put("idGenerate", agentCode);
        userParams.put("login", telephone);
        userParams.put("pwd", passwordEncoder.encode(pwd));
        userParams.put("userType", "AG");
        userParams.put("createdBy", body.get("id_user_created"));

        try {
            jdbcTemplate.update("""
                    INSERT INTO sys_users (id_generate, login, pwd, user_type, refresh, is_connect, is_active,
                        otp, is_otp_send, created_by_user_id, created_at, last_update)
                    VALUES (:idGenerate, :login, :pwd, :userType, 0, 0, 1, 0, 0, :createdBy, NOW(), NOW())
                    """, userParams);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(400, "le numéro " + telephone + " existe déjà dans notre système");
        }

        Integer userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);
        jdbcTemplate.update("""
                INSERT INTO sys_user_roles (id_users, id_role, id_user_created, created_at, last_update)
                VALUES (:userId, :roleId, :createdBy, NOW(), NOW())
                """, Map.of(
                "userId", userId,
                "roleId", Integer.parseInt(body.get("role")),
                "createdBy", agentCode));

        Map<String, Object> saved = jdbcTemplate.queryForList(
                "SELECT * FROM sys_users WHERE id = :id", Map.of("id", userId)).getFirst();
        saved.remove("pwd");
        parseLoginAsNumber(saved);

        return Map.of(
                "code", 201,
                "message", "votre compte a été créé. Veuillez vous connecter",
                "data", saved);
    }

    public Map<String, Object> getAllCompteAgent() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(ALL_ACCOUNTS_SQL, Map.of());
        return Map.of("code", 200, "message", "Comptes agents récupérés avec succès", "data", rows);
    }

    @Transactional
    public Map<String, Object> updateCompteAgent(Map<String, String> body, AuthenticatedUser user) {
        if (body.get("Agent_id") == null || body.get("Agent_id").isBlank()) {
            throw new BusinessException(400, "Identifiant requis (Agent_id)");
        }
        if (body.get("id_user_created") == null || body.get("id_user_created").isBlank()) {
            throw new BusinessException(400, "Le champ requis \"id_user_created\" est vide ou manquant.");
        }

        int agentIdNumeric = Integer.parseInt(body.get("Agent_id"));
        String agentCode = findAgentCode(agentIdNumeric);

        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT * FROM sys_users WHERE id_generate = :code LIMIT 1", Map.of("code", agentCode));
        if (users.isEmpty()) {
            throw new BusinessException(404, "Utilisateur non trouvé.");
        }
        Map<String, Object> existing = new HashMap<>(users.getFirst());
        int userId = ((Number) existing.get("id")).intValue();

        boolean hasUpdate = body.containsKey("login") || body.containsKey("pwd") || body.containsKey("role");
        if (!hasUpdate) {
            throw new BusinessException(400, "aucun champ à modifier fourni");
        }

        if (body.containsKey("pwd") || body.containsKey("pwd_repeat")) {
            String pwd = body.get("pwd");
            String pwdRepeat = body.get("pwd_repeat");
            if (pwd == null || pwd.isBlank() || pwdRepeat == null || pwdRepeat.isBlank()) {
                throw new BusinessException(400, "pwd et pwd_repeat sont requis ensemble");
            }
            if (!pwd.equals(pwdRepeat)) {
                throw new BusinessException(400, "les deux mots de passe ne matchent pas");
            }
        }

        Map<String, Object> updateData = new HashMap<>();
        String loginToSet = String.valueOf(existing.get("login"));

        if (body.containsKey("login") && body.get("login") != null) {
            String newLogin = body.get("login");
            if (!newLogin.equals(loginToSet)) {
                List<Map<String, Object>> conflict = jdbcTemplate.queryForList("""
                        SELECT id FROM sys_users WHERE login = :login AND id_generate != :code LIMIT 1
                        """, Map.of("login", newLogin, "code", agentCode));
                if (!conflict.isEmpty()) {
                    throw new BusinessException(409, "Le login '" + newLogin + "' est déjà utilisé.");
                }
                loginToSet = newLogin;
                updateData.put("login", newLogin);
            }
        }

        String pwdToSet = String.valueOf(existing.get("pwd"));
        if (body.containsKey("pwd") && body.get("pwd") != null && !body.get("pwd").isBlank()) {
            pwdToSet = passwordEncoder.encode(body.get("pwd"));
            updateData.put("pwd", body.get("pwd"));
        }

        boolean modified = !updateData.isEmpty();

        if (modified) {
            jdbcTemplate.update("""
                    UPDATE sys_users SET login = :login, pwd = :pwd, created_by_user_id = :creator, last_update = NOW()
                    WHERE id_generate = :code
                    """, Map.of(
                    "login", loginToSet,
                    "pwd", pwdToSet,
                    "creator", body.get("id_user_created"),
                    "code", agentCode));
        }

        if (body.containsKey("role") && body.get("role") != null) {
            int roleId = Integer.parseInt(body.get("role"));
            updateData.put("role", roleId);

            List<Map<String, Object>> roles = jdbcTemplate.queryForList(
                    "SELECT id, id_role FROM sys_user_roles WHERE id_users = :userId LIMIT 1",
                    Map.of("userId", userId));

            if (!roles.isEmpty()) {
                int existingRoleId = ((Number) roles.getFirst().get("id_role")).intValue();
                if (existingRoleId != roleId) {
                    jdbcTemplate.update("""
                            UPDATE sys_user_roles SET id_role = :roleId, last_update = NOW(), id_user_created = :createdBy
                            WHERE id = :id
                            """, Map.of(
                            "roleId", roleId,
                            "createdBy", agentCode,
                            "id", roles.getFirst().get("id")));
                    modified = true;
                }
            } else {
                jdbcTemplate.update("""
                        INSERT INTO sys_user_roles (id_users, id_role, id_user_created, created_at, last_update)
                        VALUES (:userId, :roleId, :createdBy, NOW(), NOW())
                        """, Map.of("userId", userId, "roleId", roleId, "createdBy", agentCode));
                modified = true;
            }
        }

        if (!modified) {
            throw new BusinessException(304, "Aucune modification effectuée");
        }

        if (!updateData.isEmpty()) {
            updateHistoryService.fillUpdateHistory(
                    agentIdNumeric, "sys_users", userId, user.getUserRf(), existing, updateData);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id_generate", agentCode);
        data.put("login", loginToSet);
        if (body.containsKey("role")) {
            data.put("role", body.get("role"));
        }

        return Map.of("code", 200, "message", "Compte mis à jour avec succès", "data", data);
    }

    @Transactional
    public Map<String, Object> disableAccount(String idGenerate) {
        if (idGenerate == null || idGenerate.isBlank()) {
            throw new BusinessException(400, "L'id_generate de l'agent est requis.");
        }
        int updated = jdbcTemplate.update("""
                UPDATE sys_users u
                JOIN op_agent a ON a.code_generate_tac = u.id_generate
                SET u.is_active = 0
                WHERE u.id_generate = :idGenerate
                """, Map.of("idGenerate", idGenerate));
        if (updated == 0) {
            throw new BusinessException(400, "compte déjà désactivé pour cet identifiant.");
        }
        return Map.of("code", 200, "message", "Compte désactivé avec succès.");
    }

    @Transactional
    public Map<String, Object> enableAccount(String idGenerate) {
        if (idGenerate == null || idGenerate.isBlank()) {
            throw new BusinessException(400, "L'id_generate de l'agent est requis.");
        }
        int updated = jdbcTemplate.update("""
                UPDATE sys_users u
                JOIN op_agent a ON a.code_generate_tac = u.id_generate
                SET u.is_active = 1
                WHERE u.id_generate = :idGenerate
                """, Map.of("idGenerate", idGenerate));
        if (updated == 0) {
            return Map.of("code", 400, "message", "compte déjà activé pour cet identifiant.");
        }
        return Map.of("code", 200, "message", "Compte activé avec succès.");
    }

    private String findAgentCode(int agentId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT code_generate_tac FROM op_agent WHERE id = :id LIMIT 1", Map.of("id", agentId));
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Agent introuvable");
        }
        return String.valueOf(rows.getFirst().get("code_generate_tac"));
    }

    private void parseLoginAsNumber(Map<String, Object> user) {
        Object login = user.get("login");
        if (login != null) {
            try {
                user.put("login", Long.parseLong(String.valueOf(login)));
            } catch (NumberFormatException ignored) {
                // conserve la valeur texte
            }
        }
    }
}
