package com.kisalu.gestion.drh.common.web;

import com.kisalu.gestion.drh.common.security.AuthenticatedUser;
import com.kisalu.gestion.drh.common.security.JwtAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Acces a l'utilisateur connecte depuis les services/controllers. */
@Component
public class AuthContext {

    public AuthenticatedUser currentUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        return (AuthenticatedUser) request.getAttribute(JwtAuthInterceptor.USER_ATTR);
    }
}
