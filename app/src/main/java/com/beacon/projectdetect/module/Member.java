package com.beacon.projectdetect.module;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiwhuang on 4/6/2017.
 */
// Member of a project
public class Member {
    private String id;
    private String name;
    private String email;
    private String role;

    public Member(DataSnapshot dataSnapshot){
        this.id = dataSnapshot.getKey();
        if (dataSnapshot.child("name").getValue() == null)
            this.name = "";
        else
            this.name = (String) dataSnapshot.child("name").getValue();
        if (dataSnapshot.child("email").getValue() == null)
            this.email = "";
        else
            this.email = (String) dataSnapshot.child("email").getValue();
        if (dataSnapshot.child("role").getValue() == null)
            this.role = "";
        else
            this.role = (String) dataSnapshot.child("role").getValue();
    }

    public Member(){
        this.name = "";
        this.email = "";
        this.role = "";
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("role", role);
        return result;
    }
}
