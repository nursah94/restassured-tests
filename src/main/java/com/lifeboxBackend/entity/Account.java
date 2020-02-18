package com.lifeboxBackend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


@Entity   //dbden istenen seyler account tablosundan ve onların get setleri
@Table(name = "ACCOUNT")

public class Account {

    @Id
    private int id;

    private BigDecimal status;
    private String username;
    private BigDecimal type;
    private int twoFactorAuth;
    private Date createdDate;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public int getTwoFactorAuth() {
        return twoFactorAuth;
    }

    public void setTwoFactorAuth(int twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

    public String getMsisdn() {
        return username;
    }

    public void setMsisdn(String msisdn) {
        this.username = msisdn;
    }

    public BigDecimal getType() {
        return type;
    }

    public void setType(BigDecimal type) {
        this.type = type;
    }

    public BigDecimal getStatus() {
        return status;
    }
    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}


