package com.kisalu.gestion.drh.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

/**
 * Enveloppe JSON standard de l'API (equivalent PHP : Router::req_response).
 * <pre>
 * Succes : { "code": 200, "message": "...", "data": {} }
 * Auth   : { "code": 200, "message": "...", "data": {}, "token": { "access": "...", "refresh": "..." } }
 * Erreur : { "code": 400, "message": "...", "error": { "code": 400, "message": "..." } }
 * </pre>
 * Le code HTTP de la reponse = champ {@code code}.
 */
@Schema(description = "Enveloppe JSON standard (équivalent PHP Router::req_response). Le code HTTP = champ code.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Schema(description = "Code HTTP (200, 201, 400, 401, 403, 404, 500…)", example = "200")
    private int code;

    @Schema(description = "Message lisible pour le client", example = "Opération réussie")
    private String message;

    @Schema(description = "Données métier (liste, objet ou null)")
    private T data;

    @Schema(description = "Détail de l'erreur (mode DEV : file, line, trace)")
    private ApiError error;

    @Schema(description = "Tokens JWT access + refresh (routes auth uniquement)")
    private TokenPair token;

    /** Reponse succes sans token */
    public static <T> ResponseEntity<ApiResponse<T>> of(int code, String message, T data) {
        return ResponseEntity.status(code).body(new ApiResponse<>(code, message, data, null, null));
    }

    /** Reponse succes avec token (login, refresh) */
    public static <T> ResponseEntity<ApiResponse<T>> of(int code, String message, T data, TokenPair token) {
        return ResponseEntity.status(code).body(new ApiResponse<>(code, message, data, null, token));
    }

    /** Reponse erreur */
    public static <T> ResponseEntity<ApiResponse<T>> error(int code, String message, ApiError error) {
        return ResponseEntity.status(code).body(new ApiResponse<>(code, message, null, error, null));
    }

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data, ApiError error, TokenPair token) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }

    public TokenPair getToken() {
        return token;
    }

    public void setToken(TokenPair token) {
        this.token = token;
    }
}
