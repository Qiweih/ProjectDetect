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
import com.beacon.projectdetect.module.Member;
import com.beacon.projectdetect.module.ProjectPlug;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiwhuang on 4/6/2017.
 */

public class EditMemberActivity extends AppCompatActivity{

    FirebaseManager firebaseManager;
    private EditText memberNameEdit;
    private EditText memberEmailEdit;
    private EditText memberRoleEdit;
    private Button save;
    private Set<Member> members = new HashSet<>();
    private boolean New;
    private String beaconIdentifier;
    private String memberName;
    private String memberEmail;
    private String memberRole;
    private String memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_member);

        // Get all Layout
        memberNameEdit = (EditText) findViewById(R.id.member_name_edit);
        memberEmailEdit = (EditText) findViewById(R.id.member_mail_edit);
        memberRoleEdit = (EditText) findViewById(R.id.member_role_edit);
        save = (Button) findViewById(R.id.save_member);
        New = getIntent().getBooleanExtra("NewMember", true);
        beaconIdentifier =  getIntent().getStringExtra("beaconIdentifierEdit");

        // If it's not a new member, set the information of the given member
        if(!New){
            memberId = getIntent().getStringExtra("memberId");
            memberName = getIntent().getStringExtra("memberName");
            memberEmail = getIntent().getStringExtra("memberEmail");
            memberRole = getIntent().getStringExtra("memberRole");

            memberNameEdit.setText(memberName);
            memberEmailEdit.setText(memberEmail);
            memberRoleEdit.setText(memberRole);
        }

        // Set listener on button save
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!memberNameEdit.getText().toString().equals("") && !memberEmailEdit.getText().toString().equals("") && !memberRoleEdit.getText().toString().equals("")) {
                    firebaseManager = FirebaseManager.getInstance();
                    // Create the map for update firebase
                    if(New) {
                        for (BeaconData beaconData : firebaseManager.dataBeacons) {
                            if (beaconData.getBeaconIdentifier().equals(beaconIdentifier)){
                                for (ProjectPlug projectPlug : beaconData.getProjectPlugs()){
                                    for (Member member : projectPlug.getMembers()){
                                        members.add(member);
                                    }
                                    Member member = new Member();
                                    member.setEmail(memberEmailEdit.getText().toString());
                                    member.setRole(memberRoleEdit.getText().toString());
                                    member.setName(memberNameEdit.getText().toString());
                                    projectPlug.getMembers().add(member);
                                    firebaseManager.createNewMemberId(projectPlug,beaconData,member);
                                    members.add(member);
                                    Map<String,Object> memberMap = firebaseManager.getMapUpdateMember(beaconData,projectPlug,members);
                                    firebaseManager.getFirebaseDatabase().getReference().updateChildren(memberMap);
                                }
                            }
                        }
                        Intent intent = new Intent(EditMemberActivity.this,ProjectPlugActivity.class);
                        intent.putExtra("beaconId", beaconIdentifier);
                        intent.putExtra("changeMember",true);
                        startActivity(intent);
                    }else {
                        for (BeaconData beaconData :firebaseManager.dataBeacons) {
                            if (beaconData.getBeaconIdentifier().equals(beaconIdentifier)){
                                for (ProjectPlug projectPlug : beaconData.getProjectPlugs()){
                                    for(Member member : projectPlug.getMembers()){
                                        if (member.getId().equals(memberId)){
                                            member.setName(memberNameEdit.getText().toString());
                                            member.setEmail(memberEmailEdit.getText().toString());
                                            member.setRole(memberRoleEdit.getText().toString());
                                            members.add(member);
                                        }
                                        members.add(member);
                                    }
                                    Map<String,Object> memberMap = firebaseManager.getMapUpdateMember(beaconData,projectPlug,members);
                                    firebaseManager.getFirebaseDatabase().getReference().updateChildren(memberMap);
                                }
                            }
                        }
                        Intent intent = new Intent(EditMemberActivity.this,ProjectPlugActivity.class);
                        intent.putExtra("beaconId", beaconIdentifier);
                        intent.putExtra("changeMember",true);
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
