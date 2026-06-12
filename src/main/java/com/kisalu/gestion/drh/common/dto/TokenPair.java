package com.kisalu.gestion.drh.common.dto;


/**
 * Paire de tokens JWT (equivalent PHP : TokenControllers::get_token).
 * access : sub=AUTH | refresh : sub=REFRESH
 */
public class TokenPair {


    /** Token d'acces API (header : Authorizations: tac &lt;access&gt;) */
    private String access;

    /** Token de renouvellement (route POST /api/v1/auth/token/refresh) */
    private String refresh;

    public TokenPair() {
    }

    public TokenPair(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }
}
