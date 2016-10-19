package com.mastercard.labs.unattended.services.dto;

/**
 * Created by douglas on 8/8/16.
 */
public class Machine {
    float distance;
    String identifier;
    String model;
    String name;
    String serial;
    String address;
    String description;
    String serviceId;

    public float getDistance() {
        return distance;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public String getSerial() {
        return serial;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceId() {
        return serviceId;
    }
}
