package com.lifeboxBackend.requestModel;

public class VerifyPhoneNumber {

    private String otp;
    private String referenceToken;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getReferenceToken() {
        return referenceToken;
    }

    public void setReferenceToken(String referenceToken) {
        this.referenceToken = referenceToken;
    }

}
