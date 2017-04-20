package com.beacon.projectdetect.module;

import com.google.firebase.database.DataSnapshot;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qiwhuang on 4/5/2017.
 */

public class BeaconData {
    private String uid;
    private String beaconIdentifier;
    private Set<ProjectPlug> projectPlugs = new HashSet<ProjectPlug>();

    public BeaconData(DataSnapshot dataSnapshot){
        this.uid = (String) dataSnapshot.getKey();
        this.beaconIdentifier = (String) dataSnapshot.child("beaconIdentifier").getValue();
        Iterable<DataSnapshot> projectsSnapshot = dataSnapshot.child("ProjectPlug").getChildren();
        if (projectsSnapshot != null) {
            for (DataSnapshot projectSnapshot : projectsSnapshot) {
                this.projectPlugs.add(new ProjectPlug(projectSnapshot));
            }
        }
    }

    public BeaconData(){
        this.beaconIdentifier = "";
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBeaconIdentifier() {
        return beaconIdentifier;
    }

    public void setBeaconIdentifier(String beaconIdentifier) {
        this.beaconIdentifier = beaconIdentifier;
    }

    public Set<ProjectPlug> getProjectPlugs() {
        return projectPlugs;
    }

    public void setProjectPlugs(Set<ProjectPlug> projectPlugs) {
        this.projectPlugs = projectPlugs;
    }

}
