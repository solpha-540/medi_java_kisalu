package com.kisalu.gestion.drh.common.util;

import com.kisalu.gestion.drh.common.exception.BusinessException;

import java.util.regex.Pattern;

/** Validations communes formulaires agent/conjoint/enfant (equivalent PHP AgentView). */
public final class AgentFormValidator {

    private static final Pattern UPPERCASE_START = Pattern.compile("^[A-ZÀ-Ö].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PHONE = Pattern.compile("^\\d{9,10}$");
    private static final Pattern REPEATED_DIGIT = Pattern.compile("^(\\d)\\1+$");

    private AgentFormValidator() {
    }

    public static void validateIdentityField(String field, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!UPPERCASE_START.matcher(trimmed).matches()) {
            throw new BusinessException(400, "Le champ \"" + field + "\" doit commencer par une majuscule.");
        }
        if (DIGIT.matcher(trimmed).matches()) {
            throw new BusinessException(400, "Le champ \"" + field + "\" ne doit pas contenir de chiffres.");
        }
    }

    public static String normalizePhone(String telephone) {
        String phone = telephone.replaceAll("\\s+", "");
        if (!PHONE.matcher(phone).matches()) {
            throw new BusinessException(400, "Le numéro de téléphone doit contenir uniquement des chiffres (9 à 10 chiffres).");
        }
        if (REPEATED_DIGIT.matcher(phone).matches()) {
            throw new BusinessException(400, "Le numéro de téléphone \"" + phone + "\" semble invalide (chiffre unique répété).");
        }
        return phone;
    }

    public static String requireNonBlank(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(400, "Le champ requis \"" + field + "\" est vide ou manquant.");
        }
        return value.trim();
    }
}
