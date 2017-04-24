package com.beacon.projectdetect.module;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by qiwhuang on 4/18/2017.
 */

// Define a beacon in user
public class Beacon {

    private String uid;
    private boolean active;
    private long dwellTime;
    private long departureTime;
    private String visitId;
    private String beaconIdentifier;

    public Beacon(DataSnapshot dataSnapshot){
        this.uid = dataSnapshot.getKey();
        this.active = (boolean) dataSnapshot.child("active").getValue();
        this.dwellTime = (long) dataSnapshot.child("dwellTime").getValue();
        this.departureTime = (long) dataSnapshot.child("departureTime").getValue();
        this.visitId = (String) dataSnapshot.child("visitId").getValue();
        this.beaconIdentifier = (String) dataSnapshot.child("beaconIdentifier").getValue();
    }

    public Beacon(){
        this.departureTime = 0;
        this.dwellTime = 0;
        this.active = false;
        this.visitId = "";
        this.beaconIdentifier = "";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public long getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(long dwellTime) {
        this.dwellTime = dwellTime;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public String getBeaconIdentifier() {
        return beaconIdentifier;
    }

    public void setBeaconIdentifier(String beaconIdentifier) {
        this.beaconIdentifier = beaconIdentifier;
    }
}
