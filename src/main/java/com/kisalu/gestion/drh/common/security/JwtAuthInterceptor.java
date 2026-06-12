package com.kisalu.gestion.drh.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisalu.gestion.drh.common.dto.ApiError;
import com.kisalu.gestion.drh.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Middleware d'authentification (equivalent ViewInterface::dispatcher).
 * Lit le header "Authorizations: tac &lt;jwt&gt;", rejette les tokens REFRESH.
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    public static final String USER_ATTR = "authenticatedUser";

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthInterceptor(JwtTokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    @Value("${app.environment:PROD}")
    private String environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (!requiresAuth(handlerMethod)) {
            return true;
        }

        String token = tokenProvider.extractTokenFromHeader(request.getHeader("Authorizations"));
        if (token == null) {
            token = tokenProvider.extractTokenFromHeader(request.getHeader("Authorization"));
        }

        String tokenType = tokenProvider.getTokenType(token);
        if (tokenType == null) {
            writeUnauthorized(response, "Le token n'est pas valide", tokenProvider.getState(), tokenProvider.getError());
            return false;
        }
        if ("refresh".equals(tokenType)) {
            writeUnauthorized(response, "Ce token est réservé au refresh", "unauthorized",
                    Map.of("state", "unauthorized", "message", "Not an access token"));
            return false;
        }

        request.setAttribute(USER_ATTR, tokenProvider.toAuthenticatedUser(tokenProvider.getDecodedToken()));
        return true;
    }

    private boolean requiresAuth(HandlerMethod handler) {
        if (handler.hasMethodAnnotation(RequiresAuth.class)) {
            return true;
        }
        return handler.getBeanType().isAnnotationPresent(RequiresAuth.class);
    }

    private void writeUnauthorized(HttpServletResponse response, String message, String state, Object details)
            throws Exception {
        ApiError error = new ApiError();
        error.setState(state);
        error.setDetails("DEV".equalsIgnoreCase(environment) ? details : null);
        ApiResponse<Void> body = new ApiResponse<>(401, message, null, error, null);
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
