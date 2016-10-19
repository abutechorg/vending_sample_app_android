package com.mastercard.labs.unattended.services.request;

/**
 * Created by douglas on 18/8/16.
 */
public class CreateSessionRequest {
    private String accessToken;
    private String refreshToken;

    public CreateSessionRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
