package com.mastercard.labs.unattended.services.dto;

/**
 * Created by douglas on 8/8/16.
 */
public class Card {
    String id;
    String alias;
    String maskedPan;
    String type;
    boolean isDefault;
    boolean isExpired;

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getType() {
        return type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isExpired() {
        return isExpired;
    }
}
