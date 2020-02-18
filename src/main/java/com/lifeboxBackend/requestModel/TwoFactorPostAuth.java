package com.lifeboxBackend.requestModel;

public class TwoFactorPostAuth {

    private boolean turkcellPasswordAuthEnabled;
    private boolean mobileNetworkAuthEnabled;
    private boolean twoFactorAuthEnabled;

    public boolean isTurkcellPasswordAuthEnabled() {
        return turkcellPasswordAuthEnabled;
    }

    public void setTurkcellPasswordAuthEnabled(boolean turkcellPasswordAuthEnabled) {
        this.turkcellPasswordAuthEnabled = turkcellPasswordAuthEnabled;
    }

    public boolean isMobileNetworkAuthEnabled() {
        return mobileNetworkAuthEnabled;
    }

    public void setMobileNetworkAuthEnabled(boolean mobileNetworkAuthEnabled) {
        this.mobileNetworkAuthEnabled = mobileNetworkAuthEnabled;
    }

    public boolean isTwoFactorAuthEnabled() {
        return twoFactorAuthEnabled;
    }

    public void setTwoFactorAuthEnabled(boolean twoFactorAuthEnabled) {
        this.twoFactorAuthEnabled = twoFactorAuthEnabled;
    }
}
