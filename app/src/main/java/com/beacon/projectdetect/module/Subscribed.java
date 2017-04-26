package com.beacon.projectdetect.module;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by qiwhuang on 4/25/2017.
 */

public class Subscribed {
    private String Uid;
    private String beaconIdentifier;
    private boolean subscribed;

    public Subscribed(DataSnapshot snapshot){
        this.Uid = snapshot.getKey();
        if (snapshot.child("beaconIdentifier").getValue() == null)
            this.beaconIdentifier = "";
        else
            beaconIdentifier = (String) snapshot.child("beaconIdentifier").getValue();
        if (snapshot.child("subscribed").getValue() == null)
            this.subscribed = false;
        else
            subscribed = (boolean) snapshot.child("subscribed").getValue();
    }

    public Subscribed(){
        this.beaconIdentifier ="";
        this.subscribed = false;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getBeaconIdentifier() {
        return beaconIdentifier;
    }

    public void setBeaconIdentifier(String beaconIdentifier) {
        this.beaconIdentifier = beaconIdentifier;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
