package com.kisalu.gestion.drh.controller;

import com.kisalu.gestion.drh.common.dto.ApiResponse;
import com.kisalu.gestion.drh.common.security.RequiresAuth;
import com.kisalu.gestion.drh.service.PrimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Module prime PHP : app/modules/prime/urls.php */
@RestController
@RequestMapping("/api/v1/prime")
@RequiresAuth
public class PrimeController {

    private final PrimeService primeService;

    public PrimeController(PrimeService primeService) {
        this.primeService = primeService;
    }

    @PostMapping("/registerPrime")
    public ResponseEntity<ApiResponse<Object>> registerPrime(@RequestBody Object body) {
        List<Map<String, Object>> items;
        if (body instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cast = (List<Map<String, Object>>) list;
            items = cast;
        } else if (body instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> single = (Map<String, Object>) map;
            items = List.of(single);
        } else {
            items = List.of();
        }
        Map<String, Object> result = primeService.createPrimes(items);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/ListePrimes", "/Prime"})
    public ResponseEntity<ApiResponse<Object>> listPrimes() {
        Map<String, Object> result = primeService.listPrimes();
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @GetMapping({"/Prime/{id}"})
    public ResponseEntity<ApiResponse<Object>> getPrime(@PathVariable Integer id) {
        Map<String, Object> result = primeService.getPrimeById(id);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }

    @PutMapping("/UpdatePrime")
    public ResponseEntity<ApiResponse<Object>> updatePrime(
            @RequestParam("prime_id") Integer primeId,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = primeService.updatePrime(primeId, body);
        return ApiResponse.of((int) result.get("code"), (String) result.get("message"), result.get("data"));
    }
}
