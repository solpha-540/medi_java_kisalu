package com.kisalu.gestion.drh.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/** Generation de codes uniques (equivalent PHP : Utils_functions::GenRefTrans). */
@Component
public class IdGenerator {

    private static final String ALPHANUM = "0123456789abcdefghijklmnopqrstuvwxyz";
    private final SecureRandom random = new SecureRandom();

    public String genRefTrans(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 16; i++) {
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
