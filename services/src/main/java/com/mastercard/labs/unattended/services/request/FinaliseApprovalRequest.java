package com.mastercard.labs.unattended.services.request;

/**
 * Created by douglas on 10/8/16.
 */
public class FinaliseApprovalRequest{
    final String payload;
    final String state;

    public FinaliseApprovalRequest(String state, String payload) {
        this.state = state;
        this.payload = payload;
    }
}
