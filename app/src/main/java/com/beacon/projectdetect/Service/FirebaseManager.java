package com.beacon.projectdetect.Service;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.beacon.projectdetect.module.Beacon;
import com.beacon.projectdetect.module.BeaconData;
import com.beacon.projectdetect.module.Member;
import com.beacon.projectdetect.module.ProjectPlug;
import com.beacon.projectdetect.module.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiwhuang on 4/5/2017.
 */

public class FirebaseManager {

    private static FirebaseManager instance ;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = "FirebaseManager";
    public Set<BeaconData> dataBeacons = new HashSet<>();
    private Callback callback;
    public Set<User> users = new HashSet<>();
    private boolean isNewBeacon = true;
    private boolean isNewUser = true;

    public FirebaseManager(final Callback callback) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        this.callback = callback;
    }

    public FirebaseUser getCurrentUser(){
        return firebaseAuth.getCurrentUser();
    }

    public void signInAnonymously(final Activity activity){
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void addAuthStateListener (){
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    public void removeAuthStateListener(){
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void retrieveBeaconData(){
        firebaseDatabase.getReference().child("BeaconData").addListenerForSingleValueEvent(new OnBeaconDataRetrieved());
    }

    public void retrieveUser(){
        firebaseDatabase.getReference().child("User").addListenerForSingleValueEvent(new OnUserDataRetrieved());
    }

    public static void createInstance(Callback callback) {
        instance = new FirebaseManager(callback);
    }

    public static FirebaseManager getInstance() {
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseStorage getFirebaseStorage() {
        return firebaseStorage;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public String createNewUserId() {
        String uid = firebaseDatabase.getReference().child("User").push().getKey();
        return uid;
    }

    public String createNewBeaconId(User user){
        String uid = firebaseDatabase.getReference().child("User").child(user.getUid()).child("history").push().getKey();
        return uid;
    }

    public String createNewBeaconDataId(BeaconData beaconData) {
        String uid = firebaseDatabase.getReference().child("BeaconData").push().getKey();
        beaconData.setUid(uid);
        return uid;
    }

    public String createNewMemberId(ProjectPlug projectPlug, BeaconData beaconData, Member member){
        String uid = firebaseDatabase.getReference().child("BeaconData").child(beaconData.getUid()).child("ProjectPlug").child(projectPlug.getProjectId()).child("member").push().getKey();
        member.setId(uid);
        return uid;
    }

    public String createNewProjectPlugId(BeaconData beaconData, ProjectPlug projectPlug){
        String uid = firebaseDatabase.getReference().child("BeaconData").child(beaconData.getUid()).child("ProjectPlug").push().getKey();
        projectPlug.setProjectId(uid);
        return uid;
    }

    public Map<String,Object> getMapUpdateMember(BeaconData beaconData, ProjectPlug projectPlug, Set<Member> members ){
            Map<String, Object> result = new HashMap<>();
            String baseKey = "BeaconData/" + beaconData.getUid() + "/ProjectPlug/" + projectPlug.getProjectId() + "/member/";

            for (Member member : members){
                String basekeyMember = baseKey + member.getId() + "/";
                result.put(basekeyMember + "name", member.getName() );
                result.put(basekeyMember + "email", member.getEmail() );
                result.put(basekeyMember + "role", member.getRole() );
            }
        return result;
    }

    public Map<String, Object> getMapUpdateProject(BeaconData beaconData, Set<ProjectPlug> projectPlugs){
            Map<String, Object> result = new HashMap<>();
            String baseKey = "BeaconData/" + beaconData.getUid() + "/ProjectPlug/";

            for (ProjectPlug projectPlug : projectPlugs){
                String basekeyMember = baseKey + projectPlug.getProjectId() + "/";
                result.put(basekeyMember + "projectName", projectPlug.getProjectName() );
                result.put(basekeyMember + "description", projectPlug.getDescription() );
                result.put(basekeyMember + "imageName", projectPlug.getImageName() );
            }
            return result;
    }

    public Map<String, Object> getMapUpdateBeacon(Set<BeaconData> beaconDatas){
        Map<String, Object> result = new HashMap<>();

        String baseKey = "BeaconData/";

        for (BeaconData beaconData : beaconDatas){
            String basekeyMember = baseKey + beaconData.getUid() + "/";
            result.put(basekeyMember + "beaconIdentifier", beaconData.getBeaconIdentifier() );
        }
        return result;
    }

    public Map<String, Object> getMapUpdateUser(Set<User> users){
        Map<String, Object> result = new HashMap<>();

        String baseKey = "User/";

        for (User user : users){
            String basekeyUser = baseKey + user.getUid() + "/";
            result.put(basekeyUser + "idUser", user.getIdUser() );
        }
        return result;
    }

    public Map<String, Object> getMapUpdateBeaconHistory(User user, Set<Beacon> beacons){
        Map<String, Object> result = new HashMap<>();

        String baseKey = "User/"  + user.getUid() + "/history/";;

        for (Beacon beacon : beacons){
            String basekeyBeacon = baseKey + beacon.getUid() + "/";
            result.put(basekeyBeacon + "active", beacon.isActive() );
            result.put(basekeyBeacon + "dwellTime", beacon.getDwellTime());
            result.put(basekeyBeacon + "departureTime", beacon.getDepartureTime());
            result.put(basekeyBeacon + "visitId", beacon.getVisitId());
            result.put(basekeyBeacon + "beaconIdentifier", beacon.getBeaconIdentifier());
        }
        return result;
    }

    private class OnBeaconDataRetrieved implements ValueEventListener{

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> dataSnapshotsBeacon = dataSnapshot.getChildren();
            if (dataSnapshotsBeacon != null) {
                for (DataSnapshot beaconDataSnapshots : dataSnapshotsBeacon) {
                    BeaconData data = new BeaconData(beaconDataSnapshots);
                    for (BeaconData beaconData : dataBeacons){
                        if (data.getUid() == beaconData.getUid()){
                            isNewBeacon = false;
                        }
                    }
                    if (isNewBeacon){
                        dataBeacons.add(data);
                    }
                }
                callback.callback();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class OnUserDataRetrieved implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> dataSnapshotsUser = dataSnapshot.getChildren();
            if(dataSnapshotsUser != null){
                for (DataSnapshot dataSnapshotUser : dataSnapshotsUser){
                    User user = new User(dataSnapshotUser);
                    for (User user1 : users){
                        if (user1.getUid() == user.getUid()){
                            isNewUser = false;
                        }
                    }
                    if(isNewUser){
                        users.add(user);
                    }
                }
                callback.callbackUser();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


    /*
    private class OnProjectDataRetrieved implements ValueEventListener{

        private String beaconId;

        public OnProjectDataRetrieved(String beaconId){
            this.beaconId = beaconId;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> dataSnapshotsProject = dataSnapshot.getChildren();
            if (dataSnapshotsProject != null) {
                for (DataSnapshot projectDataSnapshots : dataSnapshotsProject) {
                    ProjectPlug data = new ProjectPlug(projectDataSnapshots);
                    projectPlugs.add(data);
                    dataSnapshot.getRef().child(data.getProjectId()).child("member").addValueEventListener(new OnMemberDataRetrieved(data.getProjectId()));
                }
                for(BeaconData beaconData: dataBeacons){
                    if(beaconData.getUid() == beaconId){
                        beaconData.setProjectPlugs(projectPlugs);
                    }
                }

            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class OnMemberDataRetrieved implements ValueEventListener{

        private String projectId;

        public OnMemberDataRetrieved(String projectId){
            this.projectId = projectId;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> dataSnapshotsMember = dataSnapshot.getChildren();
            if (dataSnapshotsMember != null) {
                for (DataSnapshot memberDataSnapshots : dataSnapshotsMember) {
                    Member data = new Member(memberDataSnapshots);
                    members.add(data);
                }
                for(ProjectPlug projectPlug : projectPlugs){
                    if (projectPlug.getProjectId() == projectId){
                        projectPlug.setMembers(members);
                    }
                }
                callback.callback();

            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
*/


}
