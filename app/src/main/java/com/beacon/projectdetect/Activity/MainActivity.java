package com.beacon.projectdetect.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.beacon.projectdetect.Adapter.ProjectRecycleView;
import com.beacon.projectdetect.R;
import com.beacon.projectdetect.Service.AppServices;
import com.beacon.projectdetect.Service.Callback;
import com.beacon.projectdetect.Service.FirebaseManager;
import com.beacon.projectdetect.module.BeaconData;
import com.beacon.projectdetect.module.User;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  Callback, ProjectRecycleView.BeaconInteractionCallback{

    // Define variable
    private ArrayList<BeaconData> listBeacon = new ArrayList<>();
    private ProjectRecycleView adapter;
    public FirebaseManager firebaseManager;
    /*
    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private BeaconEventListener beaconEventListener;
    */
    private boolean isNewUser = true;
    private BroadcastReceiver receiver;
    private boolean changePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase configuration
        FirebaseManager.createInstance(this);
        firebaseManager = FirebaseManager.getInstance();
        firebaseManager.retrieveUser();
        firebaseManager.signInAnonymously(this);
        firebaseManager.addAuthStateListener();
        firebaseManager.retrieveBeaconData();

        //Define the broadcast in order to switch to the page of project that correspond to the beacon
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("receiver"));

        // If the photo changed, we refresh the list
        changePhoto = getIntent().getBooleanExtra("changePhoto", false);


        // Receiver of service broadcast
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("Message");
                if (message != null){
                    Intent intent1 = new Intent(MainActivity.this, ProjectPlugActivity.class);
                    startActivity(intent1);
                }
            }
        };
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
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseManager.removeAuthStateListener();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    // When finish retrieve beaconData
    @Override
    public void callback() {
        // Create the list
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_main_activity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create adapter and put it in a RecycleView
        adapter = new ProjectRecycleView(listBeacon, this, changePhoto);
        recyclerView.setAdapter(adapter);

        // put element in the list
        for (BeaconData beaconData : firebaseManager.dataBeacons){
            listBeacon.add(beaconData);
            adapter.notifyItemInserted(listBeacon.indexOf(beaconData));
        }
    }

    // Finish retrieve users
    @Override
    public void callbackUser() {
        for (User user: firebaseManager.users) {
            if(user.getIdUser().equals(firebaseManager.getCurrentUser().getUid())){
                isNewUser = false;
            }
        }
        if(isNewUser){
            User newUser = new User(firebaseManager.getCurrentUser().getUid());
            newUser.setUid(firebaseManager.createNewUserId());
            firebaseManager.users.add(newUser);
            Map<String,Object> map = firebaseManager.getMapUpdateUser(firebaseManager.users);
            firebaseManager.getFirebaseDatabase().getReference().updateChildren(map);
        }
        Intent intent = new Intent(this, AppServices.class);
        startService(intent);
    }

    // Click on a line of the list of project
    @Override
    public void onClickBeacon(BeaconData item) {
        Intent intent = new Intent(MainActivity.this, ProjectPlugActivity.class);
        intent.putExtra("beaconId", item.getBeaconIdentifier());
        intent.putExtra("changePhoto", changePhoto);
        startActivity(intent);
    }

    // Create a Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Listener of the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Toast.makeText(this, "Ajouter un projet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EditProjectActivity.class);
                intent.putExtra("New", true);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
