package com.kisalu.gestion.drh.common.exception;

/** Exception metier avec code HTTP (remplace les return ['code'=>400] du PHP). */
public class BusinessException extends RuntimeException {

    /** Code HTTP a renvoyer au client */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
