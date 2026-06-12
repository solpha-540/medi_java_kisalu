package com.kisalu.gestion.drh.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kisalu.gestion.drh.common.dto.AuthLoginResult;
import com.kisalu.gestion.drh.common.dto.LoginAdminData;
import com.kisalu.gestion.drh.common.dto.TokenPair;
import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.JwtTokenProvider;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.SysUsersRepository;

import io.jsonwebtoken.Claims;

/** Authentification admin et agent (AuthView, AgentTacView PHP). */
@Service
public class UserAuthService {

    private static final String AGENT_LOGIN_SELECT =
            "SELECT u.id, u.id_generate, u.login, u.pwd, u.is_active, u.user_type, "
            + "p.nom, p.prenom, p.sexe, p.photo, "
            + "c.telephone_un, c.telephone_deux, c.telephone_trois, "
            + "a.matricule, a.id AS agent_id, a.date_embauche, "
            + "s.id AS service_id, s.libelle AS service_libelle, "
            + "d.libele AS direction_libelle, r.libelle AS role_libelle "
            + "FROM sys_users u "
            + "INNER JOIN op_agent a ON a.code_generate_tac = u.id_generate "
            + "INNER JOIN rf_personnes p ON p.id = a.personne_id "
            + "LEFT JOIN rf_contact c ON c.id_op_agent = a.id "
            + "LEFT JOIN op_affectation_rh ar ON ar.agent_id = a.id "
            + "LEFT JOIN rf_service_rh s ON s.id = ar.service_id "
            + "LEFT JOIN rf_direction d ON d.Id = s.id_direction "
            + "LEFT JOIN sys_user_roles ur ON ur.id_users = u.id "
            + "LEFT JOIN sys_roles r ON r.id = ur.id_role "
            + "WHERE u.login = :login "
            + "LIMIT 1";

    private final SysUsersRepository usersRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public UserAuthService(
            SysUsersRepository usersRepository,
            NamedParameterJdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.usersRepository = usersRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthLoginResult loginAdmin(Map<String, String> body) {
        if (body.get("login") == null || body.get("pwd") == null) {
            throw new BusinessException(400, "ce(s) clé(s) est(sont) obligatoire : login, pwd");
        }
        SysUsers user = usersRepository.findByLogin(body.get("login"))
                .orElseThrow(() -> new BusinessException(404, "numéro de téléphone ou mot de passe incorrect"));

        if (!passwordEncoder.matches(body.get("pwd"), user.getPwd())) {
            throw new BusinessException(404, "numéro de téléphone ou mot de passe incorrect");
        }
        if (!"A".equals(user.getUserType())) {
            throw new BusinessException(400, "vous n'êtes pas autorisé à vous connecter via cet end-point");
        }

        TokenPair token = tokenProvider.generateTokens(user);
        Map<String, Object> userInfo = buildUserInfoMap(user);

        LoginAdminData data = new LoginAdminData("John", "Doe", "Rachid", "admin", userInfo);
        return new AuthLoginResult(200, "bienvenue à vous", data, token);
    }

    /** POST /api/v1/user/agent — AgentTacView PHP. */
    public Map<String, Object> loginAgent(Map<String, String> body) {
        String login = body.get("login");
        String pwd = body.get("pwd");
        if (login == null || login.isBlank()) {
            throw new BusinessException(400, "Le champ 'login' est requis.");
        }
        if (pwd == null || pwd.isBlank()) {
            throw new BusinessException(400, "Le champ 'pwd' est requis.");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                AGENT_LOGIN_SELECT, Map.of("login", login.trim()));
        if (rows.isEmpty()) {
            throw new BusinessException(400, "Identifiants invalides ou utilisateur introuvable. Veuillez réessayer.");
        }

        Map<String, Object> userRow = new HashMap<>(rows.getFirst());
        String storedPwd = String.valueOf(userRow.get("pwd"));
        if (!passwordEncoder.matches(pwd, storedPwd)) {
            throw new BusinessException(400, "Identifiants invalides ou utilisateur introuvable. Veuillez réessayer.");
        }

        Object isActive = userRow.get("is_active");
        if (isActive == null || !"1".equals(String.valueOf(isActive))) {
            throw new BusinessException(400, "Compte desactiver veillez contactez le service informatique");
        }

        if (!"AG".equals(String.valueOf(userRow.get("user_type")))) {
            throw new BusinessException(400, "Vous n'êtes pas autorisé à vous connecter via cet end-point.");
        }

        int userId = ((Number) userRow.get("id")).intValue();
        SysUsers user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(400,
                        "Identifiants invalides ou utilisateur introuvable. Veuillez réessayer."));

        userRow.remove("pwd");
        TokenPair token = tokenProvider.generateTokens(user);

        String nom = userRow.get("nom") != null ? String.valueOf(userRow.get("nom")) : "Utilisateur";
        String prenom = userRow.get("prenom") != null ? String.valueOf(userRow.get("prenom")) : "";

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Bienvenue " + nom + " " + prenom);
        result.put("data", userRow);
        result.put("token", token);
        return result;
    }

    public TokenPair refreshToken(String token) {
        if (!"refresh".equals(tokenProvider.getTokenType(token))) {
            throw new BusinessException(401, "Le token n'est pas valide");
        }
        Claims claims = tokenProvider.getDecodedToken();
        SysUsers user = usersRepository.findById(claims.get("user_id", Integer.class))
                .orElseThrow(() -> new BusinessException(401, "Le token n'est pas valide"));
        return tokenProvider.generateTokens(user);
    }

    private static Map<String, Object> buildUserInfoMap(SysUsers user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("id_generate", user.getIdGenerate());
        map.put("login", user.getLogin());
        map.put("user_type", user.getUserType());
        map.put("is_active", user.getIsActive());
        return map;
    }
}
