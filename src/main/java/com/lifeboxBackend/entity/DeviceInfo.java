package com.lifeboxBackend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "DEVICE_INFO")

public class DeviceInfo {

    @Id
    private int ıd;

    private String uuid;
    private int accountId;
    private int twoFactorVerified;
    private Date lastAuthenticatedDate;

    public Date getLastAuthenticatedDate() {
        return lastAuthenticatedDate;
    }

    public void setLastAuthenticatedDate(Date lastAuthenticatedDate) {
        this.lastAuthenticatedDate = lastAuthenticatedDate;
    }

    public int getId() {
        return ıd;
    }

    public void setId(int ıd) {
        this.ıd = ıd;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getTwoFactorVerified() {
        return twoFactorVerified;
    }

    public void setTwoFactorVerified(int twoFactorVerified) {
        this.twoFactorVerified = twoFactorVerified;
    }
}
