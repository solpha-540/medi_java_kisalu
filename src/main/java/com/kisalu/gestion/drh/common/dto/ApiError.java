package com.kisalu.gestion.drh.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Detail d'une erreur API (equivalent PHP : Router::get_error_data).
 * En mode DEV (app.environment=DEV), file/line/trace sont inclus.
 */
@Schema(description = "Détail d'une erreur API (équivalent PHP Router::get_error_data)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    @Schema(description = "Code HTTP de l'erreur", example = "400")
    private int code;

    @Schema(description = "Message d'erreur", example = "Champ requis manquant")
    private String message;

    @Schema(description = "État JWT : expired, invalid, unauthorized…", example = "expired")
    private String state;

    @Schema(description = "Détails supplémentaires (mode DEV)")
    private Object details;

    @Schema(description = "Fichier source (mode DEV)")
    private String file;

    @Schema(description = "Ligne source (mode DEV)")
    private Integer line;

    @Schema(description = "Stack trace (mode DEV)")
    private Object trace;

    public ApiError() {
    }

    public ApiError(int code, String message, String state, Object details, String file, Integer line, Object trace) {
        this.code = code;
        this.message = message;
        this.state = state;
        this.details = details;
        this.file = file;
        this.line = line;
        this.trace = trace;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Object getTrace() {
        return trace;
    }

    public void setTrace(Object trace) {
        this.trace = trace;
    }
}
