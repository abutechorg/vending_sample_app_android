package com.mastercard.labs.unattended.services;

/**
 * Created by douglas on 10/8/16.
 */
public class ServiceException extends Exception {
    private final String code;
    private final String message;
    private final Integer statusCode;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        statusCode = null;
    }

    public ServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
        this.code = null;
    }
}
