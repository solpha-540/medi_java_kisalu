package com.kisalu.gestion.drh.service;

import com.kisalu.gestion.drh.common.exception.BusinessException;
import com.kisalu.gestion.drh.repository.SysUserRolesRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Verification des roles (SysUserRolesSerializer::getAgentIdentiteEtRole).
 * Roles DRH : admin, dg, drh, chef personnel
 */
@Service
public class RoleAuthorizationService {


    private final SysUserRolesRepository userRolesRepository;

    public List<String> getRoles(Integer userId) {
        return userRolesRepository.findRoleLabelsByUserId(userId).stream()
                .map(r -> r.toLowerCase(Locale.ROOT))
                .toList();
    }

    public void requireAnyRole(Integer userId, String... allowedRoles) {
        List<String> roles = getRoles(userId);
        if (roles.isEmpty()) {
            throw new BusinessException(403, "Vous n'êtes pas autorisé à effectuer cette action");
        }
        boolean allowed = Arrays.stream(allowedRoles)
                .map(r -> r.toLowerCase(Locale.ROOT))
                .anyMatch(roles::contains);
        if (!allowed) {
            throw new BusinessException(403, "Vous n'êtes pas autorisé à effectuer cette action");
        }
    }

    public RoleAuthorizationService(SysUserRolesRepository userRolesRepository) {
        this.userRolesRepository = userRolesRepository;
    }
}
