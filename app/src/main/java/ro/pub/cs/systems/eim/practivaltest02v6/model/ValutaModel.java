package ro.pub.cs.systems.eim.practivaltest02v6.model;

import android.util.Log;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import ro.pub.cs.systems.eim.practivaltest02v6.general.Constants;

public class ValutaModel {
    private String updatedAt;
    private String eur;
    private String usd;
    private LocalTime cacheTime;

    public ValutaModel() {
        updatedAt = null;
        eur = null;
        usd = null;
        cacheTime = null;
    }

    public  ValutaModel(String updatedAt, String eur, String usd) {
        this.updatedAt = updatedAt;
        this.eur = eur;
        this.usd = usd;
        this.cacheTime = LocalTime.now();
    }

    public String GetUpdatedAt(){
        return  updatedAt;
    }
    public String GetEur(){
        return  eur;
    }
    public String GetUsd(){
        return  usd;
    }
    public boolean CanUseCache() {
        long secondsApart = Math.abs(ChronoUnit.SECONDS.between(cacheTime, LocalTime.now()));
//        return secondsApart < 10;
        Log.i(Constants.TAG, "[MODEL ] " + secondsApart);
        return false;
    }
}
