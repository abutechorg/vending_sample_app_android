package com.mastercard.labs.unattended.services.request;

/**
 * Created by douglas on 9/9/16.
 */
public class Credential {
    private String accessToken;
    private String refreshToken;

    public Credential(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
