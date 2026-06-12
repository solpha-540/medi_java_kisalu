package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.common.web.AuthContext;
import com.kisalu.gestion.drh.service.AgentAbsenceService;
import com.kisalu.gestion.drh.service.AgentAccountService;
import com.kisalu.gestion.drh.service.AgentAffectationService;
import com.kisalu.gestion.drh.service.AgentCareerService;
import com.kisalu.gestion.drh.service.AgentDossierService;
import com.kisalu.gestion.drh.service.AgentExtendedService;
import com.kisalu.gestion.drh.service.AgentPayrollService;
import com.kisalu.gestion.drh.service.AgentRegistrationService;
import com.kisalu.gestion.drh.service.AgentService;
import com.kisalu.gestion.drh.service.AgentSpecimenService;
import com.kisalu.gestion.drh.service.AgentValidationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Module agent PHP : app/modules/agent/urls.php (60+ routes DRH).
 * Gestion agents, conjoints, absences, validations DRH/DG, etc.
 */
@RestController
@RequestMapping("/api/v1/agent")
@RequiresAuth
public class AgentController {


    private final AgentService agentService;
    private final AgentRegistrationService agentRegistrationService;
    private final AgentExtendedService agentExtendedService;
    private final AgentPayrollService agentPayrollService;
    private final AgentValidationService agentValidationService;
    private final AgentAbsenceService agentAbsenceService;
    private final AgentAccountService agentAccountService;
    private final AgentDossierService agentDossierService;
    private final AgentAffectationService agentAffectationService;
    private final AgentCareerService agentCareerService;
    private final AgentSpecimenService agentSpecimenService;
    private final AuthContext authContext;

    public AgentController(
            AgentService agentService,
            AgentRegistrationService agentRegistrationService,
            AgentExtendedService agentExtendedService,
            AgentPayrollService agentPayrollService,
            AgentValidationService agentValidationService,
            AgentAbsenceService agentAbsenceService,
            AgentAccountService agentAccountService,
            AgentDossierService agentDossierService,
            AgentAffectationService agentAffectationService,
            AgentCareerService agentCareerService,
            AgentSpecimenService agentSpecimenService,
            AuthContext authContext) {
        this.agentService = agentService;
        this.agentRegistrationService = agentRegistrationService;
        this.agentExtendedService = agentExtendedService;
        this.agentPayrollService = agentPayrollService;
        this.agentValidationService = agentValidationService;
        this.agentAbsenceService = agentAbsenceService;
        this.agentAccountService = agentAccountService;
        this.agentDossierService = agentDossierService;
        this.agentAffectationService = agentAffectationService;
        this.agentCareerService = agentCareerService;
        this.agentSpecimenService = agentSpecimenService;
        this.authContext = authContext;
    }

    @GetMapping({"/ListeAgent", "/ListeAgent/{id}"})
    public ResponseEntity<ApiResponse<Object>> listAgents(@PathVariable(required = false) Integer id) {
        Map<String, Object> result = agentService.listAgents(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = {"/registerAgent", ""}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> registerAgent(
            @RequestParam String nom,
            @RequestParam("post_nom") String postNom,
            @RequestParam String prenom,
            @RequestParam String sexe,
            @RequestParam String telephone,
            @RequestParam(required = false) String mail,
            @RequestParam String quartier,
            @RequestParam String avenue,
            @RequestParam("num_parcelle") String numParcelle,
            @RequestParam("date_embauche") String dateEmbauche,
            @RequestParam String contrat,
            @RequestParam("date_naissance") String dateNaissance,
            @RequestParam("domaine_etude") String domaineEtude,
            @RequestParam("etat_civil") String etatCivil,
            @RequestParam String commune,
            @RequestParam(required = false) String id_user_created,
            @RequestParam(required = false) String telephone_deux,
            @RequestParam(required = false) String telephone_trois,
            @RequestPart("photo") MultipartFile photo) {

        Map<String, String> form = Map.ofEntries(
                Map.entry("nom", nom),
                Map.entry("post_nom", postNom),
                Map.entry("prenom", prenom),
                Map.entry("sexe", sexe),
                Map.entry("telephone", telephone),
                Map.entry("mail", mail == null ? "" : mail),
                Map.entry("quartier", quartier),
                Map.entry("avenue", avenue),
                Map.entry("num_parcelle", numParcelle),
                Map.entry("date_embauche", dateEmbauche),
                Map.entry("contrat", contrat),
                Map.entry("date_naissance", dateNaissance),
                Map.entry("domaine_etude", domaineEtude),
                Map.entry("etat_civil", etatCivil),
                Map.entry("commune", commune),
                Map.entry("id_user_created", id_user_created == null ? "" : id_user_created),
                Map.entry("telephone_deux", telephone_deux == null ? "" : telephone_deux),
                Map.entry("telephone_trois", telephone_trois == null ? "" : telephone_trois));

        Map<String, Object> result = agentRegistrationService.registerAgent(form, photo, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @DeleteMapping("/deleteAgent")
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@RequestParam Integer id) {
        Map<String, Object> result = agentRegistrationService.deleteAgent(id, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), null);
    }

    @PostMapping(value = "/updateAgent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateAgent(
            @RequestParam Integer id,
            @RequestParam(required = false) String nom,
            @RequestParam(value = "post_nom", required = false) String postNom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String sexe,
            @RequestParam(required = false) String mail,
            @RequestParam(required = false) String quartier,
            @RequestParam(required = false) String avenue,
            @RequestParam(value = "num_parcelle", required = false) String numParcelle,
            @RequestParam(value = "date_embauche", required = false) String dateEmbauche,
            @RequestParam(required = false) String contrat,
            @RequestParam(value = "date_naissance", required = false) String dateNaissance,
            @RequestParam(value = "domaine_etude", required = false) String domaineEtude,
            @RequestParam(value = "etat_civil", required = false) String etatCivil,
            @RequestParam(value = "id_fonction", required = false) String idFonction,
            @RequestParam(value = "id_grade", required = false) String idGrade,
            @RequestParam(required = false) String statut,
            @RequestParam(value = "telephone_un", required = false) String telephoneUn,
            @RequestParam(value = "telephone_deux", required = false) String telephoneDeux,
            @RequestParam(value = "telephone_trois", required = false) String telephoneTrois,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        Map<String, String> form = buildOptionalForm(nom, postNom, prenom, sexe, mail, quartier, avenue, numParcelle,
                dateEmbauche, contrat, dateNaissance, domaineEtude, etatCivil, idFonction, idGrade, statut,
                telephoneUn, telephoneDeux, telephoneTrois);
        Map<String, Object> result = agentExtendedService.updateAgent(id, form, photo, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/usersCountByRole")
    public ResponseEntity<ApiResponse<Object>> usersCountByRole() {
        Map<String, Object> result = agentExtendedService.usersCountByRole();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/registerConjoint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> registerConjoint(
            @RequestParam String nom,
            @RequestParam("post_nom") String postNom,
            @RequestParam String prenom,
            @RequestParam String sexe,
            @RequestParam String telephone,
            @RequestParam("date_naissance") String dateNaissance,
            @RequestParam("lieu_naissance") String lieuNaissance,
            @RequestParam("id_op_agent") String idOpAgent,
            @RequestPart("acte_mariage") MultipartFile acteMariage) {

        Map<String, String> form = Map.of(
                "nom", nom, "post_nom", postNom, "prenom", prenom, "sexe", sexe,
                "telephone", telephone, "date_naissance", dateNaissance,
                "lieu_naissance", lieuNaissance, "id_op_agent", idOpAgent);
        Map<String, Object> result = agentExtendedService.registerConjoint(form, acteMariage, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeConjoints", "/Conjoint", "/Conjoint/{id}"})
    public ResponseEntity<ApiResponse<Object>> listConjoints(@PathVariable(required = false) Integer id) {
        Map<String, Object> result = agentExtendedService.listConjoints(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/registerEnfant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> registerEnfant(
            @RequestParam String nom,
            @RequestParam String postnom,
            @RequestParam String prenom,
            @RequestParam String sexe,
            @RequestParam("date_naissance") String dateNaissance,
            @RequestParam("id_agent") String idAgent,
            @RequestPart("attestation_naissance") MultipartFile attestation) {

        Map<String, String> form = Map.of(
                "nom", nom, "postnom", postnom, "prenom", prenom, "sexe", sexe,
                "date_naissance", dateNaissance, "id_agent", idAgent);
        Map<String, Object> result = agentExtendedService.registerEnfant(form, attestation, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeEnfants", "/Enfant", "/Enfant/{id}"})
    public ResponseEntity<ApiResponse<Object>> listEnfants(@PathVariable(required = false) Integer id) {
        Map<String, Object> result = agentExtendedService.listEnfants(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/registerFormation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> registerFormation(
            @RequestParam("nom_formation") String nomFormation,
            @RequestParam String etablissement,
            @RequestParam String experience,
            @RequestParam String annee,
            @RequestParam("id_op_agent") String idOpAgent,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String commentaire,
            @RequestPart("document") MultipartFile document) {

        Map<String, String> form = new HashMap<>();
        form.put("nom_formation", nomFormation);
        form.put("etablissement", etablissement);
        form.put("experience", experience);
        form.put("annee", annee);
        form.put("id_op_agent", idOpAgent);
        if (statut != null) form.put("statut", statut);
        if (commentaire != null) form.put("commentaire", commentaire);

        Map<String, Object> result = agentExtendedService.registerFormation(form, document, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeFormations", "/Formation", "/Formation/{id}"})
    public ResponseEntity<ApiResponse<Object>> listFormations(@PathVariable(required = false) Integer id) {
        Map<String, Object> result = agentExtendedService.listFormations(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/UpdateConjoint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateConjoint(
            @RequestParam("conjoint_id") Integer conjointId,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String postnom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String sexe,
            @RequestParam(required = false) String id_op_agent,
            @RequestParam(required = false) String date_naissance,
            @RequestParam(required = false) String lieu_naissance,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) String acte_mariage) {
        Map<String, String> form = optionalForm(
                "nom", nom, "postnom", postnom, "prenom", prenom, "sexe", sexe,
                "id_op_agent", id_op_agent, "date_naissance", date_naissance,
                "lieu_naissance", lieu_naissance, "telephone", telephone, "acte_mariage", acte_mariage);
        Map<String, Object> result = agentExtendedService.updateConjoint(conjointId, form, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @DeleteMapping("/DeleteConjoint")
    public ResponseEntity<ApiResponse<Object>> deleteConjoint(@RequestParam(required = false) Integer id) {
        Map<String, Object> result = agentExtendedService.deleteConjoint(id, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/UpdateEnfant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateEnfant(
            @RequestParam("enfant_id") Integer enfantId,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String postnom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String sexe,
            @RequestParam(required = false) String id_agent,
            @RequestParam(required = false) String date_naissance,
            @RequestParam(required = false) String attestation_naissance) {
        Map<String, String> form = optionalForm(
                "nom", nom, "postnom", postnom, "prenom", prenom, "sexe", sexe,
                "id_agent", id_agent, "date_naissance", date_naissance,
                "attestation_naissance", attestation_naissance);
        Map<String, Object> result = agentExtendedService.updateEnfant(enfantId, form, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @DeleteMapping("/DeleteEnfant")
    public ResponseEntity<ApiResponse<Object>> deleteEnfant(@RequestParam(required = false) Integer id) {
        Map<String, Object> result = agentExtendedService.deleteEnfant(id, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/UpdateFormation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateFormation(
            @RequestParam("formation_id") Integer formationId,
            @RequestParam(required = false) String nom_formation,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String etablissement,
            @RequestParam(required = false) String annee,
            @RequestParam(required = false) String document,
            @RequestParam(required = false) String id_op_agent,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String status) {
        Map<String, String> form = optionalForm(
                "nom_formation", nom_formation, "experience", experience, "etablissement", etablissement,
                "annee", annee, "document", document, "id_op_agent", id_op_agent, "statut", statut, "status", status);
        Map<String, Object> result = agentExtendedService.updateFormation(formationId, form, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/formationbystatut")
    public ResponseEntity<ApiResponse<Object>> formationByStatut(@RequestParam(required = false) String statut) {
        Map<String, Object> result = agentExtendedService.listFormationsByStatut(statut);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/DecisionFormation")
    public ResponseEntity<ApiResponse<Object>> decisionFormation(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = agentExtendedService.decisionFormation(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/specimen", "/specimen/{id}"})
    public ResponseEntity<ApiResponse<Object>> listSpecimens(@PathVariable(required = false) Integer id) {
        Map<String, Object> result = agentSpecimenService.listSpecimens(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/specimen/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> createSpecimen(
            @RequestParam String libele,
            @RequestParam("id_user_created") String idUserCreated,
            @RequestPart("photo") MultipartFile photo) {
        Map<String, Object> result = agentSpecimenService.createSpecimen(libele, idUserCreated, photo);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/registerSalaire")
    public ResponseEntity<ApiResponse<Object>> registerSalaire(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentPayrollService.registerSalaire(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeSalaires", "/Salaire", "/Salaire/{id}"})
    public ResponseEntity<ApiResponse<Object>> listSalaires(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer agentId) {
        Integer filterId = id != null ? id : agentId;
        Map<String, Object> result = agentPayrollService.listSalaires(filterId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/registerRemuneration")
    public ResponseEntity<ApiResponse<Object>> registerRemuneration(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = agentPayrollService.registerRemuneration(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeRemunerations", "/Remuneration", "/Remuneration/{id}"})
    public ResponseEntity<ApiResponse<Object>> listRemunerations(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer agentId) {
        Integer filterId = id != null ? id : agentId;
        Map<String, Object> result = agentPayrollService.listRemunerations(filterId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/registerSecuriteSocial")
    public ResponseEntity<ApiResponse<Object>> registerSecuriteSocial(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentPayrollService.registerSecuriteSocial(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeSecuritesSociales", "/SecuriteSocial", "/SecuriteSocial/{id}"})
    public ResponseEntity<ApiResponse<Object>> listSecuriteSocial(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer agentId) {
        Integer filterId = id != null ? id : agentId;
        Map<String, Object> result = agentPayrollService.listSecuriteSocial(filterId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdateSalaire")
    public ResponseEntity<ApiResponse<Object>> updateSalaire(
            @RequestParam("salaire_id") Integer salaireId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = agentPayrollService.updateSalaire(salaireId, body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdateRemuneration")
    public ResponseEntity<ApiResponse<Object>> updateRemuneration(
            @RequestParam("remuneration_id") Integer remunerationId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = agentPayrollService.updateRemuneration(remunerationId, body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdateSecuriteSocial")
    public ResponseEntity<ApiResponse<Object>> updateSecuriteSocial(
            @RequestParam("securite_social_id") Integer securiteSocialId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = agentPayrollService.updateSecuriteSocial(securiteSocialId, body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @DeleteMapping("/SecuriteSocial/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSecuriteSocial(@PathVariable Integer id) {
        Map<String, Object> result = agentPayrollService.deleteSecuriteSocial(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), null);
    }

    @PostMapping("/validationDrh")
    public ResponseEntity<ApiResponse<Object>> validationDrh(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = agentValidationService.validationDrh(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/validationDg")
    public ResponseEntity<ApiResponse<Object>> validationDg(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = agentValidationService.validationDg(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/RegisterAbsence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> registerAbsence(
            @RequestParam String agent_id,
            @RequestParam String type_signalement,
            @RequestParam String date_debut,
            @RequestParam String date_fin,
            @RequestParam String motif,
            @RequestParam String id_user_created_at,
            @RequestParam(required = false) String heure_debut,
            @RequestParam(required = false) String heure_fin,
            @RequestParam(required = false) String commentaire_rh,
            @RequestParam(required = false) String statut,
            @RequestPart(value = "document_justificatif", required = false) MultipartFile document) {

        Map<String, String> form = new HashMap<>();
        form.put("agent_id", agent_id);
        form.put("type_signalement", type_signalement);
        form.put("date_debut", date_debut);
        form.put("date_fin", date_fin);
        form.put("motif", motif);
        form.put("id_user_created_at", id_user_created_at);
        if (heure_debut != null) form.put("heure_debut", heure_debut);
        if (heure_fin != null) form.put("heure_fin", heure_fin);
        if (commentaire_rh != null) form.put("commentaire_rh", commentaire_rh);
        if (statut != null) form.put("statut", statut);

        Map<String, Object> result = agentAbsenceService.registerAbsence(form, document);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/GetAllAbsences")
    public ResponseEntity<ApiResponse<Object>> getAllAbsences() {
        Map<String, Object> result = agentAbsenceService.getAllAbsences();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/GetAbsenceByAgent", "/GetAbsenceByAgent/{id}"})
    public ResponseEntity<ApiResponse<Object>> getAbsenceByAgent(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer agent_id) {
        Integer filterId = id != null ? id : agent_id;
        Map<String, Object> result = agentAbsenceService.getAbsenceByAgent(filterId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/GetAbsenceByService")
    public ResponseEntity<ApiResponse<Object>> getAbsenceByService(@RequestParam Integer service_id) {
        Map<String, Object> result = agentAbsenceService.getAbsenceByService(service_id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/GetAbsenceByDate")
    public ResponseEntity<ApiResponse<Object>> getAbsenceByDate(
            @RequestParam String date_debut,
            @RequestParam String date_fin) {
        Map<String, Object> result = agentAbsenceService.getAbsenceByDate(date_debut, date_fin);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdateAbsenceStatus")
    public ResponseEntity<ApiResponse<Object>> updateAbsenceStatus(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentAbsenceService.updateAbsenceStatus(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/UpdateAbsence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateAbsence(
            @RequestParam String id,
            @RequestParam String agent_id,
            @RequestParam String type_signalement,
            @RequestParam String date_debut,
            @RequestParam String date_fin,
            @RequestParam String motif,
            @RequestParam String id_user_created_at,
            @RequestParam(required = false) String heure_debut,
            @RequestParam(required = false) String heure_fin,
            @RequestParam(required = false) String commentaire_rh,
            @RequestPart(value = "document_justificatif", required = false) MultipartFile document) {

        Map<String, String> form = new HashMap<>();
        form.put("id", id);
        form.put("agent_id", agent_id);
        form.put("type_signalement", type_signalement);
        form.put("date_debut", date_debut);
        form.put("date_fin", date_fin);
        form.put("motif", motif);
        form.put("id_user_created_at", id_user_created_at);
        if (heure_debut != null) form.put("heure_debut", heure_debut);
        if (heure_fin != null) form.put("heure_fin", heure_fin);
        if (commentaire_rh != null) form.put("commentaire_rh", commentaire_rh);

        Map<String, Object> result = agentAbsenceService.updateAbsence(form, document);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/GetAbsenceSignalementById")
    public ResponseEntity<ApiResponse<Object>> getAbsenceSignalementById(
            @RequestParam("id_user_created_at") String idUserCreatedAt) {
        Map<String, Object> result = agentAbsenceService.getAbsenceSignalementById(idUserCreatedAt);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdateAbsenceSignalement")
    public ResponseEntity<ApiResponse<Object>> updateAbsenceSignalement(
            @RequestParam("absence_signalement_id") Integer signalementId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = agentAbsenceService.updateAbsenceSignalement(
                signalementId, body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/CreateCompteAgent")
    public ResponseEntity<ApiResponse<Object>> createCompteAgent(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentAccountService.createCompteAgent(body);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/GetAllCompteAgent")
    public ResponseEntity<ApiResponse<Object>> getAllCompteAgent() {
        Map<String, Object> result = agentAccountService.getAllCompteAgent();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdateCompteAgent")
    public ResponseEntity<ApiResponse<Object>> updateCompteAgent(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentAccountService.updateCompteAgent(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/DisableAccount")
    public ResponseEntity<ApiResponse<Void>> disableAccount(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentAccountService.disableAccount(body.get("id_generate"));
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), null);
    }

    @PostMapping("/EnableAccount")
    public ResponseEntity<ApiResponse<Void>> enableAccount(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentAccountService.enableAccount(body.get("id_generate"));
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), null);
    }

    @GetMapping("/dossier")
    public ResponseEntity<ApiResponse<Object>> listDossiers() {
        Map<String, Object> result = agentDossierService.listDossiers();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/createDossier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> createDossier(
            @RequestParam("id_op_agent") String idOpAgent,
            @RequestPart(value = "curiculum_vitae", required = false) MultipartFile curiculumVitae,
            @RequestPart(value = "diplome_etat", required = false) MultipartFile diplomeEtat,
            @RequestPart(value = "diplome_gradua", required = false) MultipartFile diplomeGradua,
            @RequestPart(value = "diplome_licence", required = false) MultipartFile diplomeLicence) {
        Map<String, MultipartFile> files = dossierFiles(curiculumVitae, diplomeEtat, diplomeGradua, diplomeLicence);
        Map<String, Object> result = agentDossierService.createDossier(idOpAgent, authContext.currentUser(), files);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping(value = "/updateDossier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateDossier(
            @RequestParam("dossier_id") Integer dossierId,
            @RequestPart(value = "curiculum_vitae", required = false) MultipartFile curiculumVitae,
            @RequestPart(value = "diplome_etat", required = false) MultipartFile diplomeEtat,
            @RequestPart(value = "diplome_gradua", required = false) MultipartFile diplomeGradua,
            @RequestPart(value = "diplome_licence", required = false) MultipartFile diplomeLicence) {
        Map<String, MultipartFile> files = dossierFiles(curiculumVitae, diplomeEtat, diplomeGradua, diplomeLicence);
        Map<String, Object> result = agentDossierService.updateDossier(dossierId, authContext.currentUser(), files);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/AssignAgent")
    public ResponseEntity<ApiResponse<Object>> assignAgent(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = agentAffectationService.assignAgent(body);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/ReassignAgent")
    public ResponseEntity<ApiResponse<Object>> reassignAgent(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = agentAffectationService.reassignAgent(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/registerAptitude")
    public ResponseEntity<ApiResponse<Object>> registerAptitude(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentCareerService.registerAptitude(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeAptitudes", "/Aptitude", "/Aptitude/{id}"})
    public ResponseEntity<ApiResponse<Object>> listAptitudes(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer agentId) {
        Integer filterId = id != null ? id : agentId;
        Map<String, Object> result = agentCareerService.listAptitudes(filterId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/UpdateAptitude")
    public ResponseEntity<ApiResponse<Object>> updateAptitude(
            @RequestParam("aptitude_physique_id") Integer aptitudeId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = agentCareerService.updateAptitude(aptitudeId, body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/registerCursus")
    public ResponseEntity<ApiResponse<Object>> registerCursus(@RequestBody Map<String, String> body) {
        Map<String, Object> result = agentCareerService.registerCursus(body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListeCursus", "/Cursus", "/Cursus/{id}"})
    public ResponseEntity<ApiResponse<Object>> listCursus(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer agentId) {
        Integer filterId = id != null ? id : agentId;
        Map<String, Object> result = agentCareerService.listCursus(filterId);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PostMapping("/UpdateCursus")
    public ResponseEntity<ApiResponse<Object>> updateCursus(
            @RequestParam("cursus_academique_id") Integer cursusId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = agentCareerService.updateCursus(cursusId, body, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping("/Byroles")
    public ResponseEntity<ApiResponse<Object>> getAgentsByRole() {
        Map<String, Object> result = agentCareerService.getAgentsByRole();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/historiqueAdresseAgent", "/historiqueAdresseAgent/{id}"})
    public ResponseEntity<ApiResponse<Object>> historiqueAdresseAgent(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Integer id_agent) {
        Integer filterId = id_agent != null ? id_agent : id;
        Map<String, Object> result = agentCareerService.historiqueAdresseAgent(filterId, authContext.currentUser());
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    private Map<String, MultipartFile> dossierFiles(
            MultipartFile curiculumVitae,
            MultipartFile diplomeEtat,
            MultipartFile diplomeGradua,
            MultipartFile diplomeLicence) {
        Map<String, MultipartFile> files = new HashMap<>();
        files.put("curiculum_vitae", curiculumVitae);
        files.put("diplome_etat", diplomeEtat);
        files.put("diplome_gradua", diplomeGradua);
        files.put("diplome_licence", diplomeLicence);
        return files;
    }

    private Map<String, String> buildOptionalForm(String... pairs) {
        Map<String, String> form = new HashMap<>();
        String[] keys = {"nom", "post_nom", "prenom", "sexe", "mail", "quartier", "avenue", "num_parcelle",
                "date_embauche", "contrat", "date_naissance", "domaine_etude", "etat_civil",
                "id_fonction", "id_grade", "statut", "telephone_un", "telephone_deux", "telephone_trois"};
        for (int i = 0; i < keys.length && i < pairs.length; i++) {
            if (pairs[i] != null) {
                form.put(keys[i], pairs[i]);
            }
        }
        return form;
    }

    private Map<String, String> optionalForm(String... keyValues) {
        Map<String, String> form = new HashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            if (keyValues[i + 1] != null) {
                form.put(keyValues[i], keyValues[i + 1]);
            }
        }
        return form;
    }
}
