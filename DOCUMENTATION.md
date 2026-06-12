# Documentation API DRH — Kisalu

Portage de l'API PHP Slim 4 vers **Spring Boot 3.5** pour la gestion des ressources humaines de Kisalu.

| Élément | Valeur |
|---------|--------|
| **Projet** | `com.kisalu.gestion:drh` |
| **Java** | 21 |
| **Base URL** | `http://localhost:8080/api/v1/{module}/...` |
| **Base MySQL** | `kisalu_sandBox` |
| **Référence PHP** | `c:\xampp\htdocs\app_api\app` |

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Architecture](#2-architecture)
3. [Configuration](#3-configuration)
4. [Authentification JWT](#4-authentification-jwt)
5. [Format des réponses API](#5-format-des-réponses-api)
6. [Gestion des erreurs](#6-gestion-des-erreurs)
7. [Rôles et autorisations](#7-rôles-et-autorisations)
8. [Workflow statuts agent](#8-workflow-statuts-agent)
9. [Uploads et fichiers statiques](#9-uploads-et-fichiers-statiques)
10. [Référence API par module](#10-référence-api-par-module)
11. [Structure du code](#11-structure-du-code)
12. [Démarrage et compilation](#12-démarrage-et-compilation)
13. [Modules non encore portés](#13-modules-non-encore-portés)

---

## 1. Vue d'ensemble

### Objectif

Reproduire fidèlement l'API PHP existante :

- Mêmes routes et noms d'endpoints
- Même format JSON de réponse (`code`, `message`, `data`, `error`, `token`)
- Même schéma MySQL (92+ tables, entités JPA générées)
- Même mécanisme JWT (`Authorizations: tac <token>`)

### Fonctionnalités portées

| Domaine | Description |
|---------|-------------|
| **Auth** | Connexion admin (`/auth/loginAdmin`), login agent mobile (`/user/agent`), refresh, verify |
| **Agents** | CRUD agent, photo, adresse, famille (conjoint/enfant), carrière |
| **Paie** | Salaire, rémunération, sécurité sociale, primes |
| **Validations** | Validation DRH (statut 3→4), validation DG (4→5) |
| **Absences** | Signalements d'absence, filtres, mises à jour statut |
| **Comptes** | Création / mise à jour comptes agents, activation/désactivation |
| **Dossier RH** | CV, diplômes (multipart) |
| **Affectation** | Assignation et réaffectation d'agents |
| **Formation agent** | Formations individuelles, décision DRH/DG |
| **Formation RH** | Formations collectives (`rf_formationRh`) |
| **Carrière** | Aptitude physique, cursus académique, adresses, Byroles |
| **Specimen** | Gestion des spécimens (signatures, photos) |
| **Affectation RH** | Directions, services, fonctions, grades |
| **Régions** | Provinces, villes, communes, adresses |
| **Primes** | CRUD primes |
| **Autorisations** | Autorisations journalières DRH (modifications sensibles) |

---

## 2. Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client (Frontend)                     │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTP + JWT
                             ▼
┌─────────────────────────────────────────────────────────────┐
│  Controllers  (/api/v1/{module}/...)                        │
│  @RequiresAuth sur la plupart des modules                   │
└────────────────────────────┬────────────────────────────────┘
                             │
         ┌───────────────────┼───────────────────┐
         ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ JwtAuthInterceptor│ │ GlobalException │ │ StaticResource  │
│ (preHandle)      │ │ Handler         │ │ Config          │
└─────────────────┘ └─────────────────┘ └─────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│  Services (logique métier, JDBC, transactions)              │
│  RoleAuthorizationService, UpdateHistoryService, ...        │
└────────────────────────────┬────────────────────────────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                             ▼
┌──────────────────────┐      ┌──────────────────────┐
│ NamedParameterJdbc   │      │ JPA Repositories      │
│ Template (requêtes)  │      │ (entités 93 modèles)  │
└──────────────────────┘      └──────────────────────┘
              │
              ▼
┌──────────────────────┐
│ MySQL kisalu_sandBox │
└──────────────────────┘
```

### Couches

| Couche | Package | Rôle |
|--------|---------|------|
| **Controller** | `controller/` | Mapping HTTP, validation entrées, `ApiResponse` |
| **Service** | `service/` | Règles métier, SQL, uploads, historique |
| **Model** | `model/` | Entités JPA (mapping tables existantes) |
| **Repository** | `repository/` | Accès JPA (rôles, etc.) |
| **Common** | `common/` | Sécurité, DTO, exceptions, config, utilitaires |

### Choix techniques

- **Pas de Lombok** : getters/setters explicites
- **JDBC nommé** pour la majorité des requêtes complexes (jointures PHP)
- **JPA** pour les entités et quelques repositories
- **Transactions** `@Transactional` sur les écritures
- **Spring Security** : CORS ouvert, pas de session, auth custom via intercepteur

---

## 3. Configuration

Fichier : `src/main/resources/application.properties`

### Base de données

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kisalu_sandBox?...&zeroDateTimeBehavior=convertToNull
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=none
```

> `zeroDateTimeBehavior=convertToNull` : évite les erreurs sur les dates `0000-00-00` héritées du dump PHP.

### JWT

```properties
app.jwt.secret=<clé HS256 identique au PHP>
app.jwt.duration=3600
app.jwt.algorithm=HS256
app.jwt.access-sub=AUTH
app.jwt.refresh-sub=REFRESH
app.environment=DEV
```

### Uploads

```properties
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=22MB
```

Chemins configurables via `app.upload.*` (voir [section 9](#9-uploads-et-fichiers-statiques)).

---

## 4. Authentification JWT

### Header requis

```
Authorizations: tac <access_token>
```

> Le préfixe `tac` est **obligatoire** .  
> Fallback accepté : `Authorization: Bearer <token>` ou `Authorization: tac <token>`.

### Routes publiques (sans token)

| Route | Méthode | Usage |
|-------|---------|-------|
| `/api/v1/auth/loginAdmin` | POST | Back-office admin (`user_type = A`) |
| `/api/v1/user/agent` | POST | Application mobile agent (`user_type = AG`) |
| `/api/v1/auth/token/refresh` | POST | Renouvellement JWT |

### Routes protégées

Tous les controllers annotés `@RequiresAuth` exigent un **access token** (sub = `AUTH`).  
Les **refresh tokens** (sub = `REFRESH`) sont rejetés sur les routes métier.

### Login admin (back-office)

```http
POST /api/v1/auth/loginAdmin
Content-Type: application/json

{
  "login": "0812345678",
  "pwd": "motdepasse"
}
```

Réponse :

```json
{
  "code": 200,
  "message": "bienvenue à vous",
  "data": {
    "nom": "John",
    "postnom": "Doe",
    "prenom": "Rachid",
    "role": "admin",
    "user": {
      "id": 1,
      "id_generate": "...",
      "login": "...",
      "user_type": "A",
      "is_active": 1
    }
  },
  "token": { "access": "eyJ...", "refresh": "eyJ..." }
}
```

### Login agent (mobile)

```http
POST /api/v1/user/agent
Content-Type: application/json

{
  "login": "0812345678",
  "pwd": "motdepasse"
}
```

Réponse : profil agent enrichi (matricule, service, direction, rôle) + token. Voir [§10.1b](#101b-user--apiv1user).

| Critère | `loginAdmin` | `user/agent` |
|---------|--------------|--------------|
| `user_type` | `A` | `AG` |
| Données retournées | Wrapper `LoginAdminData` | Ligne SQL jointe `op_agent` |
| Compte inactif | Non vérifié explicitement | `is_active` doit être `1` |

### Refresh

```http
POST /api/v1/auth/token/refresh
Authorizations: tac <refresh_token>
```

### Vérification

```http
GET /api/v1/auth/token/verify
Authorizations: tac <access_token>
```

### Utilisateur authentifié (côté service)

Accessible via `AuthContext.currentUser()` :

| Champ | Description |
|-------|-------------|
| `userId` | ID `sys_users` |
| `userRf` | Référence agent (`id_generate` / RF) |
| `userLogin` | Login |
| `userType` | Type utilisateur |

---

## 5. Format des réponses API

Toutes les réponses suivent l'enveloppe PHP `Router::req_response`.

### Succès

```json
{
  "code": 200,
  "message": "Message descriptif",
  "data": { }
}
```

Le **code HTTP** de la réponse = champ `code` (200, 201, etc.).

### Erreur métier

```json
{
  "code": 400,
  "message": "Champ requis manquant",
  "error": {
    "code": 400,
    "message": "Champ requis manquant"
  }
}
```

### Erreur auth (401)

```json
{
  "code": 401,
  "message": "Le token n'est pas valide",
  "error": {
    "state": "expired",
    "details": { }
  }
}
```

> En mode `DEV`, `error.details`, `file`, `line`, `trace` sont inclus.

### Auth avec token

```json
{
  "code": 200,
  "message": "...",
  "data": { },
  "token": { "access": "...", "refresh": "..." }
}
```

---

## 6. Gestion des erreurs

### Mécanisme

```
Exception levée
      │
      ├── BusinessException (code HTTP explicite)
      │         └── GlobalExceptionHandler → ApiResponse.error(code, message)
      │
      └── Exception générique
                └── GlobalExceptionHandler → 500 + trace (mode DEV)
```

### BusinessException

```java
throw new BusinessException(404, "Agent introuvable");
```

| Code | Usage typique |
|------|---------------|
| `400` | Validation, champs manquants, format invalide |
| `401` | Token invalide / expiré (intercepteur JWT) |
| `403` | Rôle insuffisant |
| `404` | Ressource introuvable |
| `500` | Erreur serveur, upload, SQL |

### GlobalExceptionHandler

Fichier : `common/exception/GlobalExceptionHandler.java`

- `@ExceptionHandler(BusinessException.class)` → code métier
- `@ExceptionHandler(Exception.class)` → 500 générique
- Mode `app.environment=DEV` : expose `file`, `line`, `trace` dans `ApiError`

### Validations courantes

| Domaine | Exemples |
|---------|----------|
| **Agent** | Nom/prénom format, téléphone, photo max 1 Mo |
| **Documents** | PDF/JPG/PNG, taille max 1,5–2 Mo |
| **Absences** | Dates cohérentes, format heure `HH:MM` ou `HH:MM:SS` |
| **Paie** | Autorisation du jour + rôle paie pour PUT |
| **Formation** | Décision `autorise` / `rejete` uniquement sur statut `pending` |

### Historique des modifications

Les mises à jour sensibles enregistrent une trace dans `rf_historique_update` via `UpdateHistoryService` :

- Tables : `rf_conjoint`, `rf_employer_enfant`, `rf_formation`, `rf_aptitude_physique`, `rf_cursus_academique`, `op_absence_signalement`, paie, etc.
- Prérequis : autorisation journalière valide (`AutorisationService.requireAutorisationToday`)

---

## 7. Rôles et autorisations

### Vérification des rôles

`RoleAuthorizationService.requireAnyRole(userId, "admin", "drh", ...)`  
Source : tables `sys_users` → `sys_user_roles` → `sys_roles`.

### Rôles par fonctionnalité

| Fonctionnalité | Rôles autorisés |
|----------------|-----------------|
| Enregistrement agent | `admin`, `chef personnel` |
| Suppression agent | `admin`, `dg`, `drh` |
| Conjoint / enfant (CRUD) | `admin`, `chef suivi carriere` |
| Formation agent | `admin`, `chef suivi carriere`, `chef formation` |
| Décision formation | `dg`, `drh` |
| Paie (register + list) | `admin`, `chef bureau paie` |
| Paie (PUT update) | + autorisation du jour |
| Validation DRH | `admin`, `drh` |
| Validation DG | `admin`, `dg` |
| Carrière (aptitude, cursus) | `admin`, `chef suivi carriere` |
| Historique adresses | `admin`, `chef personnel` |
| Autorisations DRH (module) | `admin`, `dg`, `drh` |
| Specimen | Authentifié (pas de rôle spécifique) |

### Autorisation journalière

Avant certaines modifications (PUT paie, UpdateConjoint, UpdateFormation, etc.) :

1. L'agent connecté doit avoir une entrée `rf_autorisation` avec `statut = 'valide'` et `DATE(created_at) = CURDATE()`
2. Sinon : `404 — Aucune autorisation active trouvée pour cet agent aujourd'hui`

Création via : `POST /api/v1/autorisation/createAutorisation`

---

## 8. Workflow statuts agent

Champ `op_agent.statut` — progression typique :

```
┌──────────────┐     registerAptitude      ┌──────────────┐
│  statut = 1  │ ◄─────────────────────────│  (aptitude)  │
└──────────────┘                           └──────────────┘

┌──────────────┐     registerFormation     ┌──────────────┐
│  statut = 2  │ ◄─────────────────────────│  (formation) │
└──────────────┘                           └──────────────┘

┌──────────────┐   registerRemuneration    ┌──────────────┐
│  statut = 3  │ ◄─────────────────────────│  (paie)      │
└──────┬───────┘                           └──────────────┘
       │ validationDrh
       ▼
┌──────────────┐
│  statut = 4  │
└──────┬───────┘
       │ validationDg
       ▼
┌──────────────┐
│  statut = 5  │  (agent validé DG)
└──────────────┘
```

### Formations agent (`rf_formation.statut`)

| Statut | Signification |
|--------|---------------|
| `active` | Formation active |
| `pending` | En attente de décision DRH/DG |
| `autorise` | Approuvée |
| `rejete` | Refusée |

---

## 9. Uploads et fichiers statiques

### Limites

| Type | Taille max | Extensions |
|------|------------|------------|
| Photo agent | 1 Mo | jpg, jpeg, png, webp |
| Documents (actes, attestations, formations) | 2 Mo | pdf, jpg, jpeg, png |
| Justificatif absence | 1,5 Mo | pdf, jpg, jpeg, png |
| Dossier RH (4 fichiers) | 5 Mo/fichier | pdf, jpg, jpeg, png |
| Specimen | 2 Mo | jpg, jpeg, png, gif |

### Stockage disque → URL publique

| Contenu | Dossier disque | URL publique |
|---------|----------------|--------------|
| Photos agents | `uploads/photos_agents/` | `/public/photos_agents/{fichier}` |
| Actes mariage | `uploads/photos_actemariage_agent/actes_mariage/` | `/public/photos_actemariage_agent/actes_mariage/` |
| Attestations naissance | `uploads/photos_agents/attestations/` | `/public/photos_agents/attestations/` |
| Documents formations | `uploads/photos_agents/documents_formations/` | `/public/photos_agents/documents_formations/` |
| Justificatifs absence | `uploads/documentJustificatif/` | `/public/documentJustificatif/` |
| Dossiers RH | `uploads/dossiers_agents/` | `/public/dossiers_agents/` |
| Specimens | `uploads/specimen/` | `/public/specimen/` (DB : `/specimen/...`) |

### Endpoints multipart

Utiliser `Content-Type: multipart/form-data` pour :

- `registerAgent` (photo)
- `registerConjoint`, `registerEnfant`, `registerFormation`
- `RegisterAbsence`, `UpdateAbsence`
- `createDossier`, `updateDossier`
- `specimen/create`

---

## 10. Référence API par module

> Préfixe commun : `/api/v1/{module}`  
> 🔒 = authentification requise (`@RequiresAuth`)

---

### 10.1 Auth — `/api/v1/auth`

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| POST | `/loginAdmin` | Non | Connexion administrateur (`user_type = A`) |
| POST | `/token/refresh` | Refresh token | Renouveler les tokens |
| GET | `/token/verify` | 🔒 | Vérifier validité du access token |

### 10.1b User — `/api/v1/user`

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| POST | `/agent` | Non | **Login agent mobile** (`user_type = AG`) |

**Body :**
```json
{ "login": "telephone_ou_login", "pwd": "motdepasse" }
```

**Réponse (200) :**
```json
{
  "code": 200,
  "message": "Bienvenue Nom Prenom",
  "data": {
    "id": 1, "id_generate": "...", "login": "...",
    "nom": "...", "prenom": "...", "matricule": "...",
    "agent_id": 1, "service_libelle": "...", "role_libelle": "..."
  },
  "token": { "access": "...", "refresh": "..." }
}
```

> Différent de `/auth/loginAdmin` : jointure `op_agent` + personne + service + rôle ; vérifie `is_active = 1` et `user_type = AG`.

---

### 10.2 Agent — `/api/v1/agent` 🔒

#### Agents

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/ListeAgent`, `/ListeAgent/{id}` | Liste ou détail agent |
| POST | `/registerAgent`, `/` | Créer agent (multipart + photo) |
| POST | `/updateAgent` | Mettre à jour agent (multipart) |
| DELETE | `/deleteAgent?id=` | Supprimer agent |
| GET | `/usersCountByRole` | Comptage utilisateurs par rôle |

#### Famille

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/registerConjoint` | Ajouter conjoint (multipart) |
| GET | `/ListeConjoints`, `/Conjoint`, `/Conjoint/{id}` | Lister / détail conjoint |
| POST | `/UpdateConjoint?conjoint_id=` | Modifier conjoint |
| DELETE | `/DeleteConjoint?id=` | Supprimer conjoint |
| POST | `/registerEnfant` | Ajouter enfant (multipart) |
| GET | `/ListeEnfants`, `/Enfant`, `/Enfant/{id}` | Lister / détail enfant |
| POST | `/UpdateEnfant?enfant_id=` | Modifier enfant |
| DELETE | `/DeleteEnfant?id=` | Supprimer enfant |

#### Formation agent

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/registerFormation` | Créer formation (multipart) |
| GET | `/ListeFormations`, `/Formation`, `/Formation/{id}` | Lister / détail |
| POST | `/UpdateFormation?formation_id=` | Modifier formation |
| GET | `/formationbystatut?statut=` | Filtrer par statut (`active`, `pending`, `autorise`, `rejete`) |
| POST | `/DecisionFormation` | Décision DRH/DG (`id_formation`, `statut`, `commentaire`) |

#### Paie

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/registerSalaire` | Enregistrer salaire |
| GET | `/ListeSalaires`, `/Salaire`, `/Salaire/{id}` | Lister salaires |
| PUT | `/UpdateSalaire?salaire_id=` | Modifier salaire |
| POST | `/registerRemuneration` | Enregistrer rémunération |
| GET | `/ListeRemunerations`, `/Remuneration`, `/Remuneration/{id}` | Lister rémunérations |
| PUT | `/UpdateRemuneration?remuneration_id=` | Modifier rémunération |
| POST | `/registerSecuriteSocial` | Enregistrer sécurité sociale |
| GET | `/ListeSecuritesSociales`, `/SecuriteSocial`, `/SecuriteSocial/{id}` | Lister |
| PUT | `/UpdateSecuriteSocial?securite_social_id=` | Modifier |
| DELETE | `/SecuriteSocial/delete/{id}` | Supprimer sécurité sociale |

#### Validations

| Méthode | Route | Body | Description |
|---------|-------|------|-------------|
| POST | `/validationDrh` | `{ "id_op_agent": [1, 2, 3] }` | Validation DRH (3→4) |
| POST | `/validationDg` | `{ "id_op_agent": [1, 2, 3] }` | Validation DG (4→5) |

#### Absences

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/RegisterAbsence` | Créer signalement (multipart optionnel) |
| GET | `/GetAllAbsences` | Toutes les absences (jointures service/direction) |
| GET | `/GetAbsenceByAgent/{id}`, `?agent_id=` | Par agent |
| GET | `/GetAbsenceByService?service_id=` | Par service |
| GET | `/GetAbsenceByDate?date_debut=&date_fin=` | Par période |
| PUT | `/UpdateAbsenceStatus` | Mise à jour statut seul (JSON) |
| POST | `/UpdateAbsence` | Mise à jour complète (multipart) |
| GET | `/GetAbsenceSignalementById?id_user_created_at=` | Par créateur |
| PUT | `/UpdateAbsenceSignalement?absence_signalement_id=` | Validation signalement (statut + commentaire) |

#### Comptes agents

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/CreateCompteAgent` | Créer compte |
| GET | `/GetAllCompteAgent` | Lister comptes |
| PUT | `/UpdateCompteAgent` | Modifier compte |
| POST | `/DisableAccount` | Désactiver (`id_generate`) |
| POST | `/EnableAccount` | Réactiver (`id_generate`) |

#### Dossier RH

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/dossier` | Lister dossiers |
| POST | `/createDossier` | Créer (CV + diplômes, multipart) |
| POST | `/updateDossier?dossier_id=` | Mettre à jour fichiers |

#### Affectation agent

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/AssignAgent` | Affecter agent à service/direction |
| PUT | `/ReassignAgent` | Réaffecter agent |

#### Carrière

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/registerAptitude` | Aptitude physique |
| GET | `/ListeAptitudes`, `/Aptitude`, `/Aptitude/{id}` | Lister aptitudes |
| POST | `/UpdateAptitude?aptitude_physique_id=` | Modifier aptitude |
| POST | `/registerCursus` | Cursus académique |
| GET | `/ListeCursus`, `/Cursus`, `/Cursus/{id}` | Lister cursus |
| POST | `/UpdateCursus?cursus_academique_id=` | Modifier cursus |
| GET | `/Byroles` | Agents groupés par rôle (chauffeur, vérificateur…) |
| GET | `/historiqueAdresseAgent`, `/historiqueAdresseAgent/{id}` | Adresses agents |

#### Specimen

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/specimen`, `/specimen/{id}` | Lister / détail spécimens |
| POST | `/specimen/create` | Créer (multipart : `libele`, `id_user_created`, `photo`) |

---

### 10.3 Affectation RH — `/api/v1/affectationRh` 🔒

| Ressource | GET | POST | PUT | DELETE |
|-----------|-----|------|-----|--------|
| **Direction** | `/direction`, `/direction/{segment}` | idem | `/direction/{id}` | `/directionDelete?id=` |
| **Service RH** | `/service_rh`, `/service_rh/{segment}` | idem | idem | `/service_rhDelete?id=` |
| **Fonction** | `/fonction`, `/fonction/{segment}` | idem | idem | `/fonctionDelete?id=` |
| **Grade** | `/grade`, `/grade/{segment}` | idem | `/grade/{id}` | `/gradeDelete?id=` |

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/direction/affecter-directeur` | Affecter directeur à direction |
| POST | `/service/affecter-chef` | Affecter chef de service |

---

### 10.4 Régions — `/api/v1/region` 🔒

| Ressource | GET | POST |
|-----------|-----|------|
| Province | `/province/get`, `/province/{action}` | `/province/create`, `/province/{action}` |
| Ville | `/ville/get`, `/ville/{action}` | `/ville/create`, `/ville/{action}` |
| Commune | `/commune/get`, `/commune/{action}` | `/commune/create`, `/commune/{action}` |
| Adresse | `/adresse/get`, `/adresse/{action}` | `/adresse/create`, `/adresse/{action}` |

---

### 10.5 Primes — `/api/v1/prime` 🔒

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/registerPrime` | Créer prime |
| GET | `/ListePrimes`, `/Prime`, `/Prime/{id}` | Lister / détail |
| PUT | `/UpdatePrime` | Modifier prime |

---

### 10.6 Formations RH — `/api/v1/formation` 🔒

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/`, `/create` | Créer formation(s) collective(s) |
| GET | `/all`, `/` | Toutes les formations RH |
| GET | `/by_id?id=` | Par ID |
| GET | `/agent_id?agent_id=` | Par agent |
| PUT | `/update?id=` | Modifier |
| DELETE | `/delete/{id}` | Supprimer |

---

### 10.7 Autorisations — `/api/v1/autorisation` 🔒

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/`, `?id_op_agent=` | Lister autorisations (toutes ou par agent) |
| POST | `/createAutorisation` | Créer autorisation journalière |

---

## 11. Structure du code

```
src/main/java/com/kisalu/gestion/drh/
├── DrhApplication.java
├── controller/                       (8 controllers)
│   ├── AgentController.java          (~59 routes)
│   ├── AuthController.java
│   ├── UserController.java           (login agent)
│   ├── AffectationRhController.java
│   ├── RegionController.java
│   ├── PrimeController.java
│   ├── FormationRhController.java
│   └── AutorisationController.java
├── service/
│   ├── AgentService.java
│   ├── AgentRegistrationService.java
│   ├── AgentExtendedService.java
│   ├── AgentPayrollService.java
│   ├── AgentValidationService.java
│   ├── AgentAbsenceService.java
│   ├── AgentAccountService.java
│   ├── AgentDossierService.java
│   ├── AgentAffectationService.java
│   ├── AgentCareerService.java
│   ├── AgentSpecimenService.java
│   ├── FormationRhService.java
│   ├── AutorisationService.java
│   ├── UpdateHistoryService.java
│   ├── RoleAuthorizationService.java
│   ├── UserAuthService.java
│   ├── DocumentStorage.java
│   ├── AgentPhotoStorage.java
│   └── ... (Direction, Grade, Region, Prime)
├── model/                            (93 entités JPA)
├── repository/
└── common/
    ├── config/                       (Upload, StaticResource, Security)
    ├── security/                     (JWT, Interceptor, RequiresAuth)
    ├── dto/                          (ApiResponse, ApiError, TokenPair)
    ├── exception/                    (BusinessException, GlobalExceptionHandler)
    ├── util/                         (AgentFormValidator, IdGenerator, AddressService)
    └── web/                          (WebMvcConfig, AuthContext)
```

### Services clés

| Service | Responsabilité |
|---------|----------------|
| `AgentRegistrationService` | `registerAgent`, `deleteAgent`, génération matricule |
| `AgentExtendedService` | Conjoint, enfant, formation, updates famille |
| `AgentPayrollService` | Salaire, rémunération, sécurité sociale |
| `AgentAbsenceService` | Signalements absence complets |
| `AgentCareerService` | Aptitude, cursus, Byroles, adresses |
| `UpdateHistoryService` | Audit `rf_historique_update` |
| `AutorisationService` | CRUD autorisations + `requireAutorisationToday()` |
| `DocumentStorage` | Validation et stockage fichiers génériques |
| `UserAuthService` | `loginAdmin`, `loginAgent`, `refreshToken` |

---

## 12. Démarrage et compilation

### Prérequis

- Java 21
- Maven 3.8+
- MySQL/MariaDB (XAMPP) avec base `kisalu_sandBox` importée
- Port 8080 libre (défaut Spring Boot)

### Commandes

```bash
# Compiler
mvn clean compile

# Lancer l'application
mvn spring-boot:run

# Package JAR
mvn clean package -DskipTests
```

### Test rapide

```bash
# 1a. Login admin
curl -X POST http://localhost:8080/api/v1/auth/loginAdmin \
  -H "Content-Type: application/json" \
  -d "{\"login\":\"votre_login\",\"pwd\":\"votre_mdp\"}"

# 1b. Login agent (mobile)
curl -X POST http://localhost:8080/api/v1/user/agent \
  -H "Content-Type: application/json" \
  -d "{\"login\":\"votre_login\",\"pwd\":\"votre_mdp\"}"

# 2. Appel protégé
curl http://localhost:8080/api/v1/agent/ListeAgent \
  -H "Authorizations: tac VOTRE_ACCESS_TOKEN"
```

---

## 13. Modules non encore portés

Entités JPA présentes mais **API non portée** depuis PHP :

| Module PHP | Tables associées | Priorité suggérée |
|------------|------------------|-------------------|
| User (autres routes) | `super-dealer`, `pdv`, `superuser` | Moyenne |
| Bon médical | `rf_bon_*` | Haute |
| Bus / logistique | `rf_bus`, `rf_log_bus` | Moyenne |
| Signalements techniques | `op_signalement_*` | Moyenne |
| Demandes | `rf_demande` | Moyenne |
| Clients / réservations | `rf_client`, `rf_reservation` | Basse |

Le module **agent** est quasi complet côté Spring Boot. Les prochains portages suivront le même pattern :

1. Service JDBC + règles métier PHP
2. Controller avec routes identiques
3. `@RequiresAuth` + `RoleAuthorizationService`
4. `ApiResponse.of(code, message, data)`

---

## Annexe — Codes HTTP utilisés

| Code | Signification dans l'API |
|------|--------------------------|
| 200 | Succès |
| 201 | Création (certains endpoints PHP) |
| 400 | Requête invalide, validation |
| 401 | Non authentifié / token invalide |
| 403 | Rôle insuffisant |
| 404 | Ressource introuvable |
| 500 | Erreur serveur interne |

---

*Document généré pour le projet DRH Kisalu — Spring Boot 3.5 / Java 21.*  
*Dernière mise à jour : juin 2026.*
