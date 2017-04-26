package com.beacon.projectdetect.module;

import com.google.firebase.database.DataSnapshot;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qiwhuang on 4/18/2017.
 */
// User which connect to the application
public class User {

    private String Uid;
    private String idUser;
    private Set<Beacon> beacons = new HashSet<Beacon>();
    private Set<Subscribed> listSubscrive = new HashSet<>();

    public User(DataSnapshot dataSnapshot){
        this.Uid = dataSnapshot.getKey();
        if (dataSnapshot.child("idUser").getValue() == null)
            this.idUser = "";
        else
            this.idUser = (String) dataSnapshot.child("idUser").getValue();
        Iterable<DataSnapshot> listSnapshots = dataSnapshot.child("Subscribe").getChildren();
        if (listSnapshots != null){
            for (DataSnapshot listSnapshot: listSnapshots){
                this.listSubscrive.add(new Subscribed(listSnapshot));
            }
        }
        Iterable<DataSnapshot> beaconSnapshots = dataSnapshot.child("history").getChildren();
        if (beaconSnapshots != null) {
            for (DataSnapshot beaconSnapshot : beaconSnapshots) {
                this.beacons.add(new Beacon(beaconSnapshot));
            }
        }
    }

    public User( String idUser){
        this.idUser = idUser;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Set<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(Set<Beacon> beacons) {
        this.beacons = beacons;
    }

    public Set<Subscribed> getListSubscrive() {
        return listSubscrive;
    }

    public void setListSubscrive(Set<Subscribed> listSubscrive) {
        this.listSubscrive = listSubscrive;
    }
}
