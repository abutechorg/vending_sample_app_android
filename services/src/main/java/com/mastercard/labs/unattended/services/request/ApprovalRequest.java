package com.mastercard.labs.unattended.services.request;

/**
 * Created by douglas on 10/8/16.
 */
public class ApprovalRequest {
    final String payload;
    final int amount;

    public ApprovalRequest(int amount, String payload) {
        this.amount = amount;
        this.payload = payload;
    }
}
