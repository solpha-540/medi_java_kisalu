package com.kisalu.gestion.drh.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI drhOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API DRH — Kisalu")
                        .description("""
                                Portage de l'API PHP Slim 4 vers **Spring Boot 3.5** pour la gestion des ressources humaines de Kisalu.

                                ## Informations générales
                                - **Base URL** : `/api/v1/{module}/...`
                                - **Base MySQL** : `kisalu_sandBox`
                                - **Java** : 21 | **Projet** : `com.kisalu.gestion:drh`

                                ## Authentification
                                Routes publiques : `POST /auth/loginAdmin`, `POST /user/agent`, `POST /auth/token/refresh`.
                                Routes protégées (`@RequiresAuth`) : header `Authorizations: tac <access_token>` (sub = AUTH).

                                ## Format des réponses
                                Enveloppe PHP `Router::req_response` :
                                - Succès : `{ "code": 200, "message": "...", "data": {} }`
                                - Auth : `{ ..., "token": { "access": "...", "refresh": "..." } }`
                                - Erreur : `{ "code": 400, "message": "...", "error": { ... } }`

                                Le **code HTTP** de la réponse = champ `code` (200, 201, 400, 401, 403, 404, 500).

                                ## Workflow statuts agent (`op_agent.statut`)
                                1 → aptitude | 2 → formation | 3 → paie | **validationDrh** → 4 | **validationDg** → 5

                                ## Uploads multipart
                                Photo agent (1 Mo), documents (2 Mo), justificatif absence (1,5 Mo), dossier RH (5 Mo/fichier).
                                Fichiers servis sous `/public/{type}/{fichier}`.
                                """)
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8080").description("Développement local")))
                .tags(List.of(
                        new Tag().name("Auth").description(
                                "Connexion admin (`loginAdmin`), refresh et vérification JWT. Routes publiques sauf `/token/verify`."),
                        new Tag().name("User").description(
                                "Login agent mobile (`POST /agent`, `user_type = AG`). Profil enrichi + token JWT."),
                        new Tag().name("Agent").description(
                                "Module principal (~60 routes) : agents, famille, formations, paie, validations DRH/DG, absences, comptes, dossier RH, carrière, spécimens. Authentification requise."),
                        new Tag().name("Affectation RH").description(
                                "Organisation RH : directions, services, fonctions, grades, affectation directeur/chef de service."),
                        new Tag().name("Régions").description(
                                "Référentiel géographique : provinces, villes, communes, adresses."),
                        new Tag().name("Primes").description("CRUD des primes agents."),
                        new Tag().name("Formations RH").description(
                                "Formations collectives (`rf_formationRh`) : création, liste, mise à jour par agent ou ID."),
                        new Tag().name("Autorisations").description(
                                "Autorisations journalières DRH. Requises avant certaines modifications sensibles (paie PUT, conjoint, formation…)."),
                        new Tag().name("Demandes").description(
                                "Demandes entre agents (`rf_demande`). Liées au module autorisation via `id_demande`.")
                ));
    }
}
