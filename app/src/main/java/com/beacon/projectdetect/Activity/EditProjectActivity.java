package com.beacon.projectdetect.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.beacon.projectdetect.R;
import com.beacon.projectdetect.Service.FirebaseManager;
import com.beacon.projectdetect.module.BeaconData;
import com.beacon.projectdetect.module.ProjectPlug;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiwhuang on 4/6/2017.
 */

public class EditProjectActivity extends AppCompatActivity{

    FirebaseManager firebaseManager;
    private BeaconData beaconData;
    private ProjectPlug projectPlug;
    private EditText beaconIdentifierEdit;
    private EditText projectNameEdit;
    private EditText descriptionEdit;
    private Button save;
    private Set<ProjectPlug> projectPlugs = new HashSet<>();
    private boolean New;
    private String beaconIdentifier;
    private String projectName;
    private String projectDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_project);

        // Get all Layout
        beaconIdentifierEdit = (EditText) findViewById(R.id.beaconIdentifierEdit);
        projectNameEdit = (EditText) findViewById(R.id.projectNameEdit);
        descriptionEdit = (EditText) findViewById(R.id.descriptionEdit);
        save = (Button) findViewById(R.id.save);
        New = getIntent().getBooleanExtra("New", true);

        if(!New){
            beaconIdentifier =  getIntent().getStringExtra("beaconIdentifierEdit");
            beaconIdentifierEdit.setText(beaconIdentifier);
            beaconIdentifierEdit.setEnabled(false);
            projectName = getIntent().getStringExtra("projectNameEdit");
            projectNameEdit.setText(projectName);
            projectDescription = getIntent().getStringExtra("descriptionEdit");
            descriptionEdit.setText(projectDescription);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!projectNameEdit.getText().toString().equals("") && !beaconIdentifierEdit.getText().toString().equals("") && !descriptionEdit.getText().toString().equals("")) {
                    firebaseManager = FirebaseManager.getInstance();
                    if(New) {
                        beaconData = new BeaconData();
                        beaconData.setBeaconIdentifier(beaconIdentifierEdit.getText().toString());
                        firebaseManager.createNewBeaconDataId(beaconData);

                        projectPlug = new ProjectPlug();
                        projectPlug.setDescription(descriptionEdit.getText().toString());
                        projectPlug.setProjectName(projectNameEdit.getText().toString());
                        projectPlug.setImageName(beaconIdentifierEdit.getText().toString() + ".png");
                        firebaseManager.createNewProjectPlugId(beaconData ,projectPlug);

                        firebaseManager.dataBeacons.add(beaconData);
                        projectPlugs.add(projectPlug);

                        Map<String,Object> beaconMap = firebaseManager.getMapUpdateBeacon(firebaseManager.dataBeacons);
                        Map<String,Object> projectMap = firebaseManager.getMapUpdateProject(beaconData,projectPlugs);

                        firebaseManager.getFirebaseDatabase().getReference().updateChildren(beaconMap);
                        firebaseManager.getFirebaseDatabase().getReference().updateChildren(projectMap);

                        Intent intent = new Intent(EditProjectActivity.this,ProjectPlugActivity.class);
                        intent.putExtra("beaconId", beaconIdentifierEdit.getText().toString());
                        startActivity(intent);
                    }else {
                        for (BeaconData beaconData :firebaseManager.dataBeacons) {
                            if (beaconData.getBeaconIdentifier().equals(beaconIdentifier)){
                                for (ProjectPlug projectPlug : beaconData.getProjectPlugs()){
                                    projectPlug.setProjectName(projectNameEdit.getText().toString());
                                    projectPlug.setDescription(descriptionEdit.getText().toString());
                                    projectPlugs.add(projectPlug);
                                }
                                Map<String,Object> beaconMap = firebaseManager.getMapUpdateBeacon(firebaseManager.dataBeacons);
                                Map<String,Object> projectMap = firebaseManager.getMapUpdateProject(beaconData,projectPlugs);

                                firebaseManager.getFirebaseDatabase().getReference().updateChildren(beaconMap);
                                firebaseManager.getFirebaseDatabase().getReference().updateChildren(projectMap);
                            }
                        }
                        Intent intent = new Intent(EditProjectActivity.this,ProjectPlugActivity.class);
                        intent.putExtra("beaconId", beaconIdentifier);
                        startActivity(intent);
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

}
