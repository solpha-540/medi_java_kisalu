package com.kisalu.gestion.drh.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Detail d'une erreur API (equivalent PHP : Router::get_error_data).
 * En mode DEV (app.environment=DEV), file/line/trace sont inclus.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {


    private int code;
    private String message;
    /** Etat JWT : expired, invalid, unauthorized... */
    private String state;
    private Object details;
    private String file;
    private Integer line;
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
