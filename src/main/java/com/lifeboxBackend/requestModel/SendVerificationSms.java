package com.lifeboxBackend.requestModel;

public class SendVerificationSms {

    private String referenceToken;
    private Long eulaId = 483L;
    private boolean processPersonalData = true;
    private boolean etkAuth;

    public String getReferenceToken() {
        return referenceToken;
    }

    public void setReferenceToken(String referenceToken) {
        this.referenceToken = referenceToken;
    }

    public Long getEulaId() {
        return eulaId;
    }

    public void setEulaId(Long eulaId) {
        this.eulaId = eulaId;
    }

    public boolean isProcessPersonalData() {
        return processPersonalData;
    }

    public void setProcessPersonalData(boolean processPersonalData) {
        this.processPersonalData = processPersonalData;
    }

    public boolean isEtkAuth() {
        return etkAuth;
    }

    public void setEtkAuth(boolean etkAuth) {
        this.etkAuth = etkAuth;
    }


}
