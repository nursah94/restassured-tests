package com.lifeboxBackend.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GLOBAL_SETTING")

public class GlobalSetting {

    @Id
    private Long ıd;

    private String type;
    private String value;

    public Long getId() {
        return ıd;
    }

    public void setId(Long ıd) {
        this.ıd = ıd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
