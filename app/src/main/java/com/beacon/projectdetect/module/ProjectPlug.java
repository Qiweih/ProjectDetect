package com.beacon.projectdetect.module;

import com.google.firebase.database.DataSnapshot;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qiwhuang on 4/6/2017.
 */
// Project
public class ProjectPlug {
    private String projectId;
    private String projectName;
    private Set<Member> members = new HashSet<>();
    private String description;
    private String imageName;

    public ProjectPlug(DataSnapshot dataSnapshot){
        this.projectId = dataSnapshot.getKey();
        this.projectName = (String) dataSnapshot.child("projectName").getValue();
        this.description = (String) dataSnapshot.child("description").getValue();
        this.imageName = (String) dataSnapshot.child("imageName").getValue();
        Iterable<DataSnapshot> membersSnapshot = dataSnapshot.child("member").getChildren();
        if (membersSnapshot != null) {
            for (DataSnapshot memberSnapshot : membersSnapshot) {
                this.members.add(new Member(memberSnapshot));
            }
        }
    }

    public ProjectPlug(){
        this.projectId = "";
        this.projectName = "";
        this.description = "";
        this.imageName = "";
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
