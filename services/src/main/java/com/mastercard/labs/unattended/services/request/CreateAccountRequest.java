package com.mastercard.labs.unattended.services.request;

/**
 * Created by douglas on 18/8/16.
 */
public class CreateAccountRequest {
    private String email;
    private String countryOfResidence;
    private String firstName;
    private String lastName;
    private String password;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
