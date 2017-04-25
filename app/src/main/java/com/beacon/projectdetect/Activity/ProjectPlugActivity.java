package com.beacon.projectdetect.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beacon.projectdetect.Adapter.MemberAdapter;
import com.beacon.projectdetect.R;
import com.beacon.projectdetect.Service.FirebaseManager;
import com.beacon.projectdetect.module.BeaconData;
import com.beacon.projectdetect.module.Member;
import com.beacon.projectdetect.module.ProjectPlug;
import com.beacon.projectdetect.module.Subscribed;
import com.beacon.projectdetect.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiwhuang on 4/6/2017.
 */

public class ProjectPlugActivity extends AppCompatActivity {

    private TextView beaconId;
    private TextView projectName;
    private TextView description;
    private ListView members;
    private List<Member> membersList = new ArrayList<Member>();
    private FirebaseManager firebaseManager;
    private StorageReference storageReference;
    private ImageView imageView;
    private String beaconIdtext;
    private String name;
    private String projectDescription;
    private String projectId;
    private String beaconUid;
    private boolean changePhoto;
    private Button subscription;
    private Button stopSubscription;
    private boolean isNewSubscribed = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_plug);

        // Get all Layout
        beaconId = (TextView) findViewById(R.id.beacon_id);
        projectName = (TextView) findViewById(R.id.project_name);
        description = (TextView) findViewById(R.id.project_description);
        members = (ListView) findViewById(R.id.list_membre);
        imageView = (ImageView) findViewById(R.id.image_project);
        subscription = (Button) findViewById(R.id.subscription);
        stopSubscription = (Button) findViewById(R.id.subscription_stop);
        firebaseManager = FirebaseManager.getInstance();

        // Depend if it's a service which triggered this activity or a click
        if (getIntent().getStringExtra("beaconId") != null){
            beaconIdtext = getIntent().getStringExtra("beaconId");
        }
        else {
            beaconIdtext = SplashActivity.BeaconId;
        }
        beaconId.setText(beaconIdtext);

        changePhoto = getIntent().getBooleanExtra("changePhoto",false);

        storageReference = firebaseManager.getFirebaseStorage().getReference();

        if (firebaseManager.dataBeacons != null){
            for(BeaconData beaconData: firebaseManager.dataBeacons){
                if (beaconData.getBeaconIdentifier().equals(beaconIdtext)){
                    beaconUid = beaconData.getUid();
                    for (ProjectPlug projectPlug : beaconData.getProjectPlugs()){
                        projectId = projectPlug.getProjectId();
                        name = projectPlug.getProjectName();
                        projectDescription = projectPlug.getDescription();
                        projectName.setText(projectPlug.getProjectName());
                        description.setText(projectPlug.getDescription());
                        for(Member member : projectPlug.getMembers()){
                            membersList.add(member);
                        }
                        MemberAdapter adapter = new MemberAdapter(this,membersList, projectPlug.getProjectId(), beaconData.getUid(), firebaseManager, beaconIdtext);
                        members.setAdapter(adapter);
                        // Get the image on Firebase with Firebase UI
                        if (!projectPlug.getImageName().equals("")){
                            storageReference = storageReference.child("BeaconData").child(beaconData.getUid()).child("ProjectPlug").child(projectPlug.getProjectId()).child(projectPlug.getImageName());
                            if (changePhoto)
                                Glide.with(this)
                                        .using(new FirebaseImageLoader())
                                        .load(storageReference)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .placeholder(R.drawable.project)
                                        .into(imageView);
                            else
                                Glide.with(this)
                                        .using(new FirebaseImageLoader())
                                        .load(storageReference)
                                        .placeholder(R.drawable.project)
                                        .into(imageView);
                        }

                    }
                }
            }
        }

        for(User user : firebaseManager.users){
            if (user.getIdUser().equals(firebaseManager.getCurrentUser().getUid())){
                for(Subscribed id : user.getListSubscrive()){
                    if(id.getBeaconIdentifier().equals(beaconIdtext)){
                        if(id.isSubscribed()){
                            subscription.setEnabled(false);
                            stopSubscription.setEnabled(true);
                        }else {
                            subscription.setEnabled(true);
                            stopSubscription.setEnabled(false);
                        }
                    }
                }
            }
        }

        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSubscription.setEnabled(true);
                subscription.setEnabled(false);
                String msg = new String( "Vous vous êtes abonné à "+ projectName.getText().toString() + " !");
                FirebaseMessaging.getInstance().subscribeToTopic(projectName.getText().toString());
                Toast.makeText(ProjectPlugActivity.this, msg, Toast.LENGTH_SHORT).show();
                for (User user : firebaseManager.users){
                    if(user.getIdUser().equals(firebaseManager.getCurrentUser().getUid())){
                        for(Subscribed subscribed : user.getListSubscrive()){
                            if(subscribed.getBeaconIdentifier().equals(beaconIdtext)){
                                subscribed.setSubscribed(true);
                                isNewSubscribed = false;
                            }
                        }
                        Set<Subscribed> subscribeds = user.getListSubscrive();
                        if(isNewSubscribed){
                            Subscribed subscribed = new Subscribed();
                            subscribed.setUid(firebaseManager.createNewSubscribeId(user));
                            subscribed.setBeaconIdentifier(beaconIdtext);
                            subscribed.setSubscribed(true);
                            subscribeds.add(subscribed);
                        }
                        user.setListSubscrive(subscribeds);
                        Map<String,Object> map = firebaseManager.getMapUpdateBeaconHistory(user,user.getBeacons(),user.getListSubscrive());
                        firebaseManager.getFirebaseDatabase().getReference().updateChildren(map);
                    }
                }

            }
        });

        stopSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.setEnabled(true);
                stopSubscription.setEnabled(false);
                String msg = new String( "Vous vous êtes désabonné à "+ projectName.getText().toString() + " !");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(projectName.getText().toString());
                Toast.makeText(ProjectPlugActivity.this, msg, Toast.LENGTH_SHORT).show();
                for (User user : firebaseManager.users){
                    if(user.getIdUser().equals(firebaseManager.getCurrentUser().getUid())){
                        for(Subscribed subscribed : user.getListSubscrive()){
                            if(subscribed.getBeaconIdentifier().equals(beaconIdtext)){
                                subscribed.setSubscribed(false);
                            }
                        }
                        Map<String,Object> map = firebaseManager.getMapUpdateBeaconHistory(user,user.getBeacons(),user.getListSubscrive());
                        firebaseManager.getFirebaseDatabase().getReference().updateChildren(map);
                    }
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onRestart(){
        super.onRestart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    // Back to Main
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_projet, menu);
        return true;
    }


    // Menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(this, "Editer un projet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, EditProjectActivity.class);
                intent.putExtra("New", false);
                intent.putExtra("beaconIdentifierEdit",beaconIdtext);
                intent.putExtra("projectNameEdit",name);
                intent.putExtra("descriptionEdit",projectDescription);
                startActivity(intent);
                break;
            case R.id.action_member:
                Toast.makeText(this, "Ajouter un membre", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, EditMemberActivity.class);
                intent2.putExtra("NewMember", true);
                intent2.putExtra("beaconIdentifierEdit",beaconIdtext);
                startActivity(intent2);
                break;
            case  R.id.action_photo:
                Intent intent3 = new Intent(this, AddPhotoActivity.class);
                intent3.putExtra("NewPhoto", true);
                intent3.putExtra("beaconIdentifierEdit",beaconIdtext);
                intent3.putExtra("beaconUid",beaconUid);
                intent3.putExtra("projectId",projectId);
                startActivity(intent3);
                break;
            default:
                break;
        }
        return true;
    }
}
