package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.common.util.IdGenerator;
import com.kisalu.gestion.drh.repository.OpAgentRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** Enregistrement et suppression agents (equivalent AgentView + OpAgentSerializer). */
@Service
public class AgentRegistrationService {

    private static final List<String> REGISTER_REQUIRED = List.of(
            "nom", "post_nom", "prenom", "sexe", "telephone", "quartier", "avenue", "num_parcelle",
            "date_embauche", "contrat", "date_naissance", "domaine_etude", "etat_civil", "commune");
    private static final Pattern UPPERCASE_START = Pattern.compile("^[A-ZÀ-Ö].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PHONE = Pattern.compile("^\\d{9,10}$");
    private static final Pattern REPEATED_DIGIT = Pattern.compile("^(\\d)\\1+$");

    private final OpAgentRepository agentRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleAuthorizationService roleAuthorizationService;
    private final AgentPhotoStorage photoStorage;
    private final AddressService addressService;
    private final IdGenerator idGenerator;

    public AgentRegistrationService(
            OpAgentRepository agentRepository,
            NamedParameterJdbcTemplate jdbcTemplate,
            RoleAuthorizationService roleAuthorizationService,
            AgentPhotoStorage photoStorage,
            AddressService addressService,
            IdGenerator idGenerator) {
        this.agentRepository = agentRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.roleAuthorizationService = roleAuthorizationService;
        this.photoStorage = photoStorage;
        this.addressService = addressService;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public Map<String, Object> registerAgent(Map<String, String> form, MultipartFile photo, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "chef personnel");

        for (String field : REGISTER_REQUIRED) {
            if (isBlank(form.get(field))) {
                throw new BusinessException(400, "Le champ requis \"" + field + "\" est vide ou manquant.");
            }
        }

        String nom = trim(form.get("nom"));
        String postNom = trim(form.get("post_nom"));
        String prenom = trim(form.get("prenom"));

        if (agentRepository.existsByIdentity(nom, postNom, prenom)) {
            Map<String, Object> conflict = new HashMap<>();
            conflict.put("code", 409);
            conflict.put("message", "Un agent avec le même nom, post-nom et prénom existe déjà");
            conflict.put("data", Map.of("nom", nom, "post_nom", postNom, "prenom", prenom));
            return conflict;
        }

        validateIdentityField("nom", nom);
        validateIdentityField("post_nom", postNom);
        validateIdentityField("prenom", prenom);

        String telephone = form.get("telephone").replaceAll("\\s+", "");
        if (!PHONE.matcher(telephone).matches()) {
            throw new BusinessException(400, "Le numéro de téléphone doit contenir uniquement des chiffres (9 à 10 chiffres).");
        }
        if (REPEATED_DIGIT.matcher(telephone).matches()) {
            throw new BusinessException(400, "Le numéro de téléphone \"" + telephone + "\" semble invalide (chiffre unique répété).");
        }

        String photoPath = photoStorage.storeAgentPhoto(photo);
        String userCreatedRf = user.getUserRf();
        Integer communeId = parseInt(form.get("commune"), "commune");

        Map<String, Object> address = addressService.createOrFindByCommune(communeId, userCreatedRf);
        Integer adresseId = ((Number) address.get("id")).intValue();

        String codeGenerateTac = idGenerator.genRefTrans("AGTAC");
        String mail = isBlank(form.get("mail")) ? telephone + "@kisalu.cd" : trim(form.get("mail"));
        LocalDate dateNaissance = parseDate(form.get("date_naissance"), "date_naissance");
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();

        Map<String, Object> personneParams = new HashMap<>();
        personneParams.put("nom", nom);
        personneParams.put("postNom", postNom);
        personneParams.put("prenom", prenom);
        personneParams.put("sexe", trim(form.get("sexe")));
        personneParams.put("adresseId", adresseId);
        personneParams.put("photo", photoPath);
        personneParams.put("mail", mail);
        personneParams.put("userCreated", userCreatedRf);
        personneParams.put("quartier", trim(form.get("quartier")));
        personneParams.put("avenue", trim(form.get("avenue")));
        personneParams.put("numParcelle", trim(form.get("num_parcelle")));

        jdbcTemplate.update("""
                INSERT INTO rf_personnes (nom, post_nom, prenom, sexe, adresse_id, photo, mail,
                    id_user_created, quartier, avenue, num_parcelle, created_at, last_update)
                VALUES (:nom, :postNom, :prenom, :sexe, :adresseId, :photo, :mail,
                    :userCreated, :quartier, :avenue, :numParcelle, NOW(), NOW())
                """, personneParams);

        Integer personneId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);

        Map<String, Object> agentParams = new HashMap<>();
        agentParams.put("codeGenerateTac", codeGenerateTac);
        agentParams.put("personneId", personneId);
        agentParams.put("dateEmbauche", form.get("date_embauche"));
        agentParams.put("contrat", trim(form.get("contrat")));
        agentParams.put("dateNaissance", form.get("date_naissance"));
        agentParams.put("age", age);
        agentParams.put("domaineEtude", trim(form.get("domaine_etude")));
        agentParams.put("etatCivil", trim(form.get("etat_civil")));
        agentParams.put("userCreated", userCreatedRf);
        agentParams.put("statut", 0);

        jdbcTemplate.update("""
                INSERT INTO op_agent (code_generate_tac, personne_id, date_embauche, contrat, date_naissance,
                    age, domaine_etude, etat_civil, id_user_created, statut, created_at, last_update)
                VALUES (:codeGenerateTac, :personneId, :dateEmbauche, :contrat, :dateNaissance,
                    :age, :domaineEtude, :etatCivil, :userCreated, :statut, NOW(), NOW())
                """, agentParams);

        Integer agentId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Map.of(), Integer.class);

        Map<String, Object> contactParams = new HashMap<>();
        contactParams.put("telephone", telephone);
        contactParams.put("telephoneDeux", blankToNull(form.get("telephone_deux")));
        contactParams.put("telephoneTrois", blankToNull(form.get("telephone_trois")));
        contactParams.put("mail", mail);
        contactParams.put("agentId", agentId);
        contactParams.put("userCreated", userCreatedRf);

        jdbcTemplate.update("""
                INSERT INTO rf_contact (telephone_un, telephone_deux, telephone_trois, email,
                    id_op_agent, id_user_created, created_at, last_update)
                VALUES (:telephone, :telephoneDeux, :telephoneTrois, :mail,
                    :agentId, :userCreated, NOW(), NOW())
                """, contactParams);

        Map<String, Object> data = new HashMap<>();
        data.put("agent_id", agentId);
        data.put("personne_id", personneId);
        data.put("code_generate_tac", codeGenerateTac);
        data.put("date_embauche", form.get("date_embauche"));
        data.put("contrat", trim(form.get("contrat")));
        data.put("date_naissance", form.get("date_naissance"));
        data.put("age", age);
        data.put("domaine_etude", trim(form.get("domaine_etude")));
        data.put("etat_civil", trim(form.get("etat_civil")));
        data.put("id_user_created", userCreatedRf);
        data.put("nom", nom);
        data.put("post_nom", postNom);
        data.put("prenom", prenom);
        data.put("sexe", trim(form.get("sexe")));
        data.put("telephone_un", telephone);
        data.put("telephone_deux", blankToNull(form.get("telephone_deux")));
        data.put("telephone_trois", blankToNull(form.get("telephone_trois")));
        data.put("adresse_id", adresseId);
        data.put("photo", photoPath);
        data.put("mail", mail);
        data.put("quartier", trim(form.get("quartier")));
        data.put("avenue", trim(form.get("avenue")));
        data.put("num_parcelle", trim(form.get("num_parcelle")));
        data.put("statut", 0);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 201);
        result.put("message", "Agent créé avec succès.");
        result.put("data", data);
        return result;
    }

    @Transactional
    public Map<String, Object> deleteAgent(Integer id, AuthenticatedUser user) {
        roleAuthorizationService.requireAnyRole(user.getUserId(), "admin", "dg", "drh");

        if (id == null || id <= 0) {
            throw new BusinessException(400, "ID d'agent manquant ou invalide");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT op_agent.personne_id AS personneId, sys_users.id AS userId
                FROM op_agent
                LEFT JOIN sys_users ON sys_users.id_generate = op_agent.code_generate_tac
                WHERE op_agent.id = :id
                """, Map.of("id", id));

        if (rows.isEmpty()) {
            throw new BusinessException(404, "Agent introuvable");
        }

        Map<String, Object> row = rows.getFirst();
        Number userId = (Number) row.get("userId");
        Number personneId = (Number) row.get("personneId");

        if (userId != null) {
            jdbcTemplate.update("DELETE FROM sys_user_roles WHERE id_users = :userId", Map.of("userId", userId.intValue()));
            jdbcTemplate.update("DELETE FROM sys_users WHERE id = :userId", Map.of("userId", userId.intValue()));
        }
        jdbcTemplate.update("DELETE FROM rf_contact WHERE id_op_agent = :id", Map.of("id", id));
        jdbcTemplate.update("DELETE FROM op_agent WHERE id = :id", Map.of("id", id));
        if (personneId != null) {
            jdbcTemplate.update("DELETE FROM rf_personnes WHERE id = :personneId", Map.of("personneId", personneId.intValue()));
        }

        return Map.of("code", 200, "message", "Suppression complète effectuée");
    }

    private void validateIdentityField(String field, String value) {
        if (!UPPERCASE_START.matcher(value).matches()) {
            throw new BusinessException(400, "Le champ \"" + field + "\" doit commencer par une majuscule.");
        }
        if (DIGIT.matcher(value).matches()) {
            throw new BusinessException(400, "Le champ \"" + field + "\" ne doit pas contenir de chiffres.");
        }
    }

    private LocalDate parseDate(String value, String field) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(400, "Le champ \"" + field + "\" a un format de date invalide.");
        }
    }

    private Integer parseInt(String value, String field) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(400, "Le champ \"" + field + "\" doit être un nombre.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
