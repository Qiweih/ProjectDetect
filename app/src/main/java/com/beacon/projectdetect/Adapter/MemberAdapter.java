package com.beacon.projectdetect.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beacon.projectdetect.Activity.EditMemberActivity;
import com.beacon.projectdetect.Activity.MainActivity;
import com.beacon.projectdetect.R;
import com.beacon.projectdetect.Service.FirebaseManager;
import com.beacon.projectdetect.module.Member;

import java.util.List;

/**
 * Created by qiwhuang on 4/11/2017.
 */

public class MemberAdapter extends BaseAdapter {

    private Context context;
    private List<Member> members;
    private String projectId;
    private String beaconDataId;
    private FirebaseManager firebaseManager;
    private String beaconName;

    // Constructor of the list of member in project page
    public MemberAdapter(Context context, List<Member> members, String projectId, String beaconDataId, FirebaseManager firebaseManager, String beaconName){
        this.context = context;
        this.members = members;
        this.projectId = projectId;
        this.beaconDataId = beaconDataId;
        this.firebaseManager = firebaseManager;
        this.beaconName = beaconName;
    }

    // Define the viewholder in order to configure the button related
    static class ViewHolder {
        RelativeLayout container;
        TextView memberName;
        TextView memberEmail;
        TextView memberRole;
        Button delete;
        Button edit;
        GestureDetectorCompat mDetector;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Member member = members.get(position);

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.list_member, null);

            final ViewHolder holder = new ViewHolder();
            // Define the variable of holder
            holder.container = (RelativeLayout) convertView
                    .findViewById(R.id.container);
            // Define the gesture detector with the class mygesturelistener
            holder.mDetector = new GestureDetectorCompat(context,
                    new MyGestureListener(context, convertView));
            holder.memberName = (TextView) convertView.findViewById(R.id.member_name);
            holder.memberEmail = (TextView) convertView.findViewById(R.id.member_mail);
            holder.memberRole = (TextView)  convertView.findViewById(R.id.member_role);
            holder.edit = (Button) convertView.findViewById(R.id.edit);
            holder.delete = (Button) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.container.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holder.mDetector.onTouchEvent(event);
                return true;
            }
        });
        // Listener of the delete button
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Etes vous sûr de supprimer ce membre?")
                        .setTitle("Alerte");

                // Add the buttons
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseManager.getFirebaseDatabase().getReference().child("BeaconData").child(beaconDataId).child("ProjectPlug").child(projectId).child("member").child(member.getId()).removeValue();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                });
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        // Listener of the edit button
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditMemberActivity.class);
                intent.putExtra("NewMember",false);
                intent.putExtra("beaconIdentifierEdit",beaconName);
                intent.putExtra("memberId",member.getId());
                intent.putExtra("memberName",member.getName());
                intent.putExtra("memberEmail",member.getEmail());
                intent.putExtra("memberRole", member.getRole());
                context.startActivity(intent);
            }
        });

        holder.memberName.setText(member.getName());
        holder.memberEmail.setText(member.getEmail());
        holder.memberRole.setText(member.getRole());

        return convertView;
    }
}
