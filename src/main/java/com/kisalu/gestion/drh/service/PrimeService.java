package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.model.RfPrime;
import com.kisalu.gestion.drh.model.SysUsers;
import com.kisalu.gestion.drh.repository.RfPrimeRepository;
import com.kisalu.gestion.drh.repository.SysUsersRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** CRUD primes RH (equivalent PrimeView + RfPrimeSerializer). */
@Service
public class PrimeService {

    private static final Set<String> ALLOWED_TYPES = Set.of("permanent", "non_permanent", "special");

    private final RfPrimeRepository primeRepository;
    private final SysUsersRepository usersRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PrimeService(
            RfPrimeRepository primeRepository,
            SysUsersRepository usersRepository,
            NamedParameterJdbcTemplate jdbcTemplate) {
        this.primeRepository = primeRepository;
        this.usersRepository = usersRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Map<String, Object> createPrimes(List<Map<String, Object>> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException(400, "Aucune prime fournie");
        }

        List<Map<String, Object>> created = new ArrayList<>();
        for (Map<String, Object> item : items) {
            String libele = stringVal(item.get("libele"));
            String typePrime = stringVal(item.get("type_prime"));
            String idUserCreated = stringVal(item.get("id_user_created"));
            BigDecimal montant = decimalVal(item.get("montant"));

            if (libele.isBlank() || typePrime.isBlank() || idUserCreated.isBlank() || montant == null) {
                throw new BusinessException(400, "Les champs libele, montant, type_prime, id_user_created sont obligatoires");
            }
            if (!ALLOWED_TYPES.contains(typePrime)) {
                throw new BusinessException(400, "Le champ \"type_prime\" doit être \"permanent\", \"non_permanent\" ou \"special\".");
            }

            SysUsers creator = usersRepository.findByIdGenerate(idUserCreated)
                    .orElseThrow(() -> new BusinessException(400, "Utilisateur créateur introuvable"));

            RfPrime prime = new RfPrime();
            prime.setLibele(libele.trim());
            prime.setMontant(montant);
            prime.setTypePrime(typePrime);
            prime.setIdUserCreated(creator);
            prime.setCreatedAt(LocalDateTime.now());
            prime.setLastUpdate(LocalDateTime.now());
            RfPrime saved = primeRepository.save(prime);
            created.add(toMap(saved));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", created.size() > 1 ? "Primes créées avec succès." : "Prime créée avec succès.");
        result.put("data", created);
        return result;
    }

    public Map<String, Object> listPrimes() {
        List<Map<String, Object>> primes = jdbcTemplate.queryForList("SELECT * FROM rf_prime", Map.of());
        return Map.of("code", 200, "message", "Primes récupérées avec succès.", "data", primes);
    }

    public Map<String, Object> getPrimeById(Integer id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM rf_prime WHERE id = :id", Map.of("id", id));
        if (rows.isEmpty()) {
            throw new BusinessException(404, "Aucune prime trouvée pour cet ID");
        }
        return Map.of("code", 200, "message", "Prime trouvée", "data", rows);
    }

    @Transactional
    public Map<String, Object> updatePrime(Integer id, Map<String, Object> body) {
        RfPrime prime = primeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Prime non trouvé"));

        if (body.containsKey("libele")) {
            prime.setLibele(stringVal(body.get("libele")).trim());
        }
        if (body.containsKey("montant")) {
            prime.setMontant(decimalVal(body.get("montant")));
        }
        if (body.containsKey("type_prime")) {
            String type = stringVal(body.get("type_prime"));
            if (!ALLOWED_TYPES.contains(type)) {
                throw new BusinessException(400, "Type de prime invalide");
            }
            prime.setTypePrime(type);
        }
        prime.setLastUpdate(LocalDateTime.now());
        RfPrime saved = primeRepository.save(prime);
        return Map.of("code", 200, "message", "Les informations ont été mises à jour", "data", toMap(saved));
    }

    private Map<String, Object> toMap(RfPrime prime) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", prime.getId());
        map.put("libele", prime.getLibele());
        map.put("montant", prime.getMontant());
        map.put("type_prime", prime.getTypePrime());
        map.put("created_at", prime.getCreatedAt());
        map.put("last_update", prime.getLastUpdate());
        if (prime.getIdUserCreated() != null) {
            map.put("id_user_created", prime.getIdUserCreated().getIdGenerate());
        }
        return map;
    }

    private String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private BigDecimal decimalVal(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        return new BigDecimal(String.valueOf(value));
    }
}
