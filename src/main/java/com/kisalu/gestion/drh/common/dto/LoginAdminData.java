package com.kisalu.gestion.drh.common.dto;

import java.util.Map;

/** Donnees utilisateur renvoyees apres loginAdmin. */
public record LoginAdminData(
        String nom,
        String postnom,
        String prenom,
        String role,
        Map<String, Object> userInfo
) {
}
