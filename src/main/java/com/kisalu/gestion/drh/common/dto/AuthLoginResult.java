package com.kisalu.gestion.drh.common.dto;

/** Resultat interne du service loginAdmin. */
public record AuthLoginResult(
        int code,
        String message,
        LoginAdminData data,
        TokenPair token
) {
}
