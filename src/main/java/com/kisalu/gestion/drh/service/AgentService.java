package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.repository.OpAgentRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Liste/detail agents (AgentView + OpAgentSerializer). Tables : op_agent, rf_personnes */
@Service
public class AgentService {


    private static final String ALL_AGENTS_SQL = """
            SELECT
                u.id_generate AS id_generate,
                u.login AS login,
                r.libelle AS role_libelle,
                u.user_type AS user_type,
                a.id AS agentId,
                a.code_generate_tac AS codeGenerateTac,
                a.date_embauche AS dateEmbauche,
                a.contrat AS contrat,
                a.date_naissance AS dateNaissance,
                a.age AS age,
                a.domaine_etude AS domaineEtude,
                a.matricule AS matricule,
                a.etat_civil AS etatCivil,
                a.created_at AS agentCreatedAt,
                a.last_update AS agentLastUpdate,
                a.statut AS statut,
                p.nom AS nom,
                p.post_nom AS postNom,
                p.prenom AS prenom,
                p.sexe AS sexe,
                p.mail AS mail,
                p.photo AS photo,
                p.quartier AS quartier,
                p.avenue AS avenue,
                p.num_parcelle AS numParcelle,
                c.telephone_un AS telephone_un,
                c.telephone_deux AS telephone_deux,
                c.telephone_trois AS telephone_trois,
                f.libelle AS fonctionLibelle,
                g.libelle AS gradeLibelle,
                g.salaire AS gradeSalaire,
                sal.numero_compte AS numeroCompte,
                sal.nom_banque AS nomBanque,
                arh.service_id AS serviceRh_id,
                serh.libelle AS serviceLibelle,
                serh.id_direction AS serviceRh_id_direction,
                dir.libele AS directionLibelle
            FROM op_agent a
            LEFT JOIN rf_personnes p ON a.personne_id = p.id
            LEFT JOIN sys_users u ON u.id_generate = a.code_generate_tac
            LEFT JOIN sys_user_roles ur ON ur.id_users = u.id
            LEFT JOIN sys_roles r ON r.id = ur.id_role
            LEFT JOIN rf_contact c ON c.id_op_agent = a.id
            LEFT JOIN rf_fonction f ON a.id_fonction = f.id
            LEFT JOIN rf_grade g ON a.id_grade = g.id
            LEFT JOIN rf_salaire sal ON sal.id_op_agent = a.id
            LEFT JOIN op_affectation_rh arh ON arh.agent_id = a.id
            LEFT JOIN rf_service_rh serh ON serh.id = arh.service_id
            LEFT JOIN rf_direction dir ON dir.Id = serh.id_direction
            """;

    private static final String AGENT_BY_ID_SQL = """
            SELECT
                a.id AS agentId,
                a.code_generate_tac AS codeGenerateTac,
                a.date_embauche AS dateEmbauche,
                a.contrat AS contrat,
                a.date_naissance AS dateNaissance,
                a.age AS age,
                a.domaine_etude AS domaineEtude,
                a.matricule AS matricule,
                a.etat_civil AS etatCivil,
                a.created_at AS agentCreatedAt,
                a.last_update AS agentLastUpdate,
                a.statut AS statut,
                p.nom AS nom,
                p.post_nom AS postNom,
                p.prenom AS prenom,
                p.sexe AS sexe,
                p.mail AS mail,
                p.photo AS photo,
                p.quartier AS quartier,
                p.avenue AS avenue,
                p.num_parcelle AS numParcelle,
                f.libelle AS fonctionLibelle,
                g.libelle AS gradeLibelle,
                g.salaire AS gradeSalaire,
                s.libelle AS serviceLibelle,
                d.libele AS directionLibelle,
                sal.numero_compte AS numeroCompte,
                sal.nom_banque AS nomBanque
            FROM op_agent a
            LEFT JOIN rf_personnes p ON a.personne_id = p.id
            LEFT JOIN rf_fonction f ON a.id_fonction = f.id
            LEFT JOIN rf_grade g ON a.id_grade = g.id
            LEFT JOIN rf_service_rh s ON f.id_service = s.id
            LEFT JOIN rf_direction d ON s.id_direction = d.Id
            LEFT JOIN rf_salaire sal ON sal.id_op_agent = a.id
            WHERE a.id = :agentId
            LIMIT 1
            """;

    private final OpAgentRepository agentRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Map<String, Object> listAgents(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(AGENT_BY_ID_SQL, Map.of("agentId", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Agent introuvable");
            }
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "Agent trouvé");
            result.put("data", rows.getFirst());
            return result;
        }

        List<Map<String, Object>> agents = jdbcTemplate.queryForList(ALL_AGENTS_SQL, Map.of());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Liste des agents");
        result.put("data", agents);
        return result;
    }

    public AgentService(OpAgentRepository agentRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.agentRepository = agentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
}
