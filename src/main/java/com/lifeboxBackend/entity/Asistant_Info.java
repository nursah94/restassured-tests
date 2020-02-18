package com.lifeboxBackend.entity;  //objeler ile dbdeki queryin sonuçlarını eşleştirir entity oluştururken new java class methodlar küçük classlar büyük harf

import javax.persistence.Entity; //repo oluştururken java class açılanm pop-upda interface seç
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ASSISTANT_INFO")  //dbdeki tabonun adı
public class Asistant_Info {

    @Id //dbnin primariy keyi ilgili tablonun
    private int id;

    private int account_id;
    private int saved;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }



}
