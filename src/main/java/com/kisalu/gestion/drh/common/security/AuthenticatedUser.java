package com.kisalu.gestion.drh.common.security;


/** Utilisateur extrait du JWT (equivalent PHP : $request->getAttribute('user')). */
public class AuthenticatedUser {


    private String userLogin;
    private String userRf;
    private Integer userId;
    private String userType;

    public AuthenticatedUser() {
    }

    public AuthenticatedUser(String userLogin, String userRf, Integer userId, String userType) {
        this.userLogin = userLogin;
        this.userRf = userRf;
        this.userId = userId;
        this.userType = userType;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserRf() {
        return userRf;
    }

    public void setUserRf(String userRf) {
        this.userRf = userRf;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
