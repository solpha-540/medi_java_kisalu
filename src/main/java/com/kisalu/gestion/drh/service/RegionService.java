package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.model.RfCommune;
import com.kisalu.gestion.drh.model.RfProvince;
import com.kisalu.gestion.drh.model.RfVille;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.RfCommuneRepository;
import com.kisalu.gestion.drh.repository.RfProvinceRepository;
import com.kisalu.gestion.drh.repository.RfVilleRepository;
import com.kisalu.gestion.drh.repository.SysUsersRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Gestion regions : province, ville, commune (equivalent module region PHP). */
@Service
public class RegionService {

    private final RfProvinceRepository provinceRepository;
    private final RfVilleRepository villeRepository;
    private final RfCommuneRepository communeRepository;
    private final SysUsersRepository usersRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AddressService addressService;

    public RegionService(
            RfProvinceRepository provinceRepository,
            RfVilleRepository villeRepository,
            RfCommuneRepository communeRepository,
            SysUsersRepository usersRepository,
            NamedParameterJdbcTemplate jdbcTemplate,
            AddressService addressService) {
        this.provinceRepository = provinceRepository;
        this.villeRepository = villeRepository;
        this.communeRepository = communeRepository;
        this.usersRepository = usersRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.addressService = addressService;
    }

    public Map<String, Object> listProvinces() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_province", Map.of());
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Aucune province trouvée");
        }
        return Map.of("code", 200, "message", "Voici la liste des provinces", "data", rows);
    }

    @Transactional
    public Map<String, Object> createProvince(Map<String, String> body, AuthenticatedUser user) {
        String label = body.get("label");
        if (label == null || label.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : label");
        }
        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf())
                .orElseThrow(() -> new BusinessException(400, "Utilisateur introuvable"));

        RfProvince province = new RfProvince();
        province.setLabele(label.trim());
        province.setIdUserCreatedAt(creator);
        province.setCreatedAt(LocalDateTime.now());
        province.setLastUpdate(LocalDateTime.now());
        RfProvince saved = provinceRepository.save(province);
        return Map.of("code", 201, "message", "Province créé avec succès", "data", saved);
    }

    public Map<String, Object> listVilles(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM rf_ville WHERE Id = :id", Map.of("id", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Ville introuvable");
            }
            return Map.of("code", 200, "message", "Ville trouvée", "data", rows);
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_ville", Map.of());
        return Map.of("code", 200, "message", "Liste des villes", "data", rows);
    }

    @Transactional
    public Map<String, Object> createVille(Map<String, String> body, AuthenticatedUser user) {
        String label = body.get("label");
        String provinceId = body.get("province");
        if (label == null || label.isBlank() || provinceId == null || provinceId.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : label, province");
        }
        RfProvince province = provinceRepository.findById(Integer.parseInt(provinceId))
                .orElseThrow(() -> new BusinessException(400, "Province introuvable"));
        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf())
                .orElseThrow(() -> new BusinessException(400, "Utilisateur introuvable"));

        RfVille ville = new RfVille();
        ville.setLabele(label.trim());
        ville.setIdProvince(province);
        ville.setIdUserCreatedAt(creator);
        ville.setCreatedAt(LocalDateTime.now());
        ville.setLastUpdate(LocalDateTime.now());
        RfVille saved = villeRepository.save(ville);
        return Map.of("code", 201, "message", "Ville créée avec succès", "data", saved);
    }

    public Map<String, Object> listCommunes(Integer id) {
        if (id != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM rf_commune WHERE Id = :id", Map.of("id", id));
            if (rows.isEmpty()) {
                throw new BusinessException(404, "Commune introuvable");
            }
            return Map.of("code", 200, "message", "Commune trouvée", "data", rows);
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM rf_commune", Map.of());
        return Map.of("code", 200, "message", "Liste des communes", "data", rows);
    }

    @Transactional
    public Map<String, Object> createCommune(Map<String, String> body, AuthenticatedUser user) {
        String label = body.get("label");
        String villeId = body.get("ville");
        if (label == null || label.isBlank() || villeId == null || villeId.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : label, ville");
        }
        RfVille ville = villeRepository.findById(Integer.parseInt(villeId))
                .orElseThrow(() -> new BusinessException(400, "Ville introuvable"));
        SysUsers creator = usersRepository.findByIdGenerate(user.getUserRf())
                .orElseThrow(() -> new BusinessException(400, "Utilisateur introuvable"));

        RfCommune commune = new RfCommune();
        commune.setLabele(label.trim());
        commune.setIdVille(ville);
        commune.setIdUserCreatedAt(creator);
        commune.setCreatedAt(LocalDateTime.now());
        commune.setLastUpdate(LocalDateTime.now());
        RfCommune saved = communeRepository.save(commune);
        return Map.of("code", 201, "message", "Commune créée avec succès", "data", saved);
    }

    public Map<String, Object> listAddresses() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM op_adresse", Map.of());
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Aucune adresse trouvée");
        }
        return Map.of("code", 200, "message", "Voici la liste des adresses", "data", rows);
    }

    @Transactional
    public Map<String, Object> createAddress(Map<String, String> body, AuthenticatedUser user) {
        String commune = body.get("commune");
        if (commune == null || commune.isBlank()) {
            throw new BusinessException(400, "Les clés suivantes sont manquantes : commune");
        }
        Map<String, Object> created = addressService.createOrFindByCommune(
                Integer.parseInt(commune), user.getUserRf());
        return Map.of("code", 201, "message", "Adresse créée avec succès", "data", created);
    }
}
