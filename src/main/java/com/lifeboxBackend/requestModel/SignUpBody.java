package com.lifeboxBackend.requestModel;

public class SignUpBody {

    private String email;
    private String password = "135246";
    private String phoneNumber;
    private String language = "tr";
    private Boolean sendOtp = false;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getSendOtp() {
        return sendOtp;
    }

    public void setSendOtp(Boolean sendOtp) {
        this.sendOtp = sendOtp;
    }
}
