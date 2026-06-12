package com.kisalu.gestion.drh.common.exception;

import com.kisalu.gestion.drh.common.dto.ApiError;
import com.kisalu.gestion.drh.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Convertit les exceptions en reponses ApiResponse (format PHP uniforme). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.environment:PROD}")
    private String environment;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage(),
                new ApiError(ex.getCode(), ex.getMessage(), null, null, null, null, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        ApiError error = new ApiError();
        error.setCode(500);
        error.setMessage(ex.getMessage());
        if ("DEV".equalsIgnoreCase(environment)) {
            error.setFile(ex.getStackTrace().length > 0 ? ex.getStackTrace()[0].getFileName() : null);
            error.setLine(ex.getStackTrace().length > 0 ? ex.getStackTrace()[0].getLineNumber() : null);
            error.setTrace(ex.getStackTrace());
        }
        return ApiResponse.error(500, "Erreur serveur", error);
    }
}
