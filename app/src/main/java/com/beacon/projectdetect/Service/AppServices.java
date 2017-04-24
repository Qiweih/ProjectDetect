package com.beacon.projectdetect.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.beacon.projectdetect.Activity.MainActivity;
import com.beacon.projectdetect.Activity.SplashActivity;
import com.beacon.projectdetect.R;
import com.beacon.projectdetect.module.Beacon;
import com.beacon.projectdetect.module.User;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by qiwhuang on 4/14/2017.
 */

// Service that scan for beacons in background
public class AppServices extends Service {

    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private boolean isNewBeacon = true;
    private LocalBroadcastManager broadcaster;
    private String result = "receiver";
    private FirebaseManager firebaseManager;
    private PowerManager pw;
    private PowerManager.WakeLock wl;

    @Override
    public void onCreate(){
        // Define the service in foreground in order to not killed it when we switch to home and lock the screen
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ProjectDetect")
                .setContentText("Scan for beacons")
                .setContentIntent(pendingIntent).build();
        startForeground(1337, notification);

        firebaseManager = FirebaseManager.getInstance();
        broadcaster = LocalBroadcastManager.getInstance(this);
        // Allow the lock of screen
        pw = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pw.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wl.acquire();
        // Setup Gimbal
        Gimbal.setApiKey(this.getApplication(),"90ef3b9f-a6c3-4740-b494-e809fd96b8bf");
        Gimbal.registerForPush("172261695721");
        setupGimbalPlaceManager();
        setupGimabalCommunicationManager();
        Gimbal.start();
    }

    private void setupGimabalCommunicationManager() {
        // Listener for communication which create notification
        communicationListener = new CommunicationListener() {
            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> collection, Visit visit) {
                Log.d("Normal","notif");
                return super.presentNotificationForCommunications(collection, visit);
            }

            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> collection, Push push) {
                Log.d("Push","notif");
                return super.presentNotificationForCommunications(collection, push);
            }

            @Override
            public void onNotificationClicked(List<Communication> list) {
                super.onNotificationClicked(list);
            }

            @Override
            public Notification.Builder prepareCommunicationForDisplay(Communication communication, Visit visit, int i) {
                return super.prepareCommunicationForDisplay(communication, visit, i);
            }

            @Override
            public Notification.Builder prepareCommunicationForDisplay(Communication communication, Push push, int i) {
                return super.prepareCommunicationForDisplay(communication, push, i);
            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);
    }

    private void setupGimbalPlaceManager() {
        // Listener for Beacons
        placeEventListener = new PlaceEventListener() {
            @Override
            public void onVisitEnd(Visit visit) {
                // If the visit is finished, we set the visit to false on firebase
                for (User user : firebaseManager.users){
                    if (user.getIdUser().equals(firebaseManager.getCurrentUser().getUid())){
                        for (Beacon beacon : user.getBeacons()){
                            if (beacon.getVisitId().equals(visit.getVisitID())){
                                beacon.setActive(false);
                                beacon.setDepartureTime(visit.getDepartureTimeInMillis());
                                beacon.setDwellTime(visit.getDwellTimeInMillis());
                                Map<String, Object> map = firebaseManager.getMapUpdateBeaconHistory(user,user.getBeacons());
                                firebaseManager.getFirebaseDatabase().getReference().updateChildren(map);
                            }
                        }
                    }
                }
                // Set the beacon id (static variable) to "" in order to allow automatic switch to project
                SplashActivity.BeaconId = "";
                super.onVisitEnd(visit);
            }

            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
                super.onBeaconSighting(beaconSighting, list);
                // If the sighting that we detect is not the same beacon that before, we execute the code below
                if(!list.isEmpty()){
                    if (!SplashActivity.BeaconId.equals(beaconSighting.getBeacon().getIdentifier())){
                        if(beaconSighting.getRSSI() > -55){
                            for (User user : firebaseManager.users) {
                                if (user.getIdUser().equals(firebaseManager.getCurrentUser().getUid())) {
                                    for (Beacon beacon : user.getBeacons()){
                                        for (Visit visit : list){
                                            if (beacon.getVisitId().equals(visit.getVisitID())){
                                                if(beacon.isActive()){
                                                    isNewBeacon = false;
                                                }
                                            }
                                        }
                                    }
                                    if (isNewBeacon){
                                        for (Beacon beacon :user.getBeacons()){
                                            if(beacon.isActive()){
                                                beacon.setActive(false);
                                            }
                                        }
                                        Beacon beacon = new Beacon();
                                        beacon.setUid(firebaseManager.createNewBeaconId(user));
                                        beacon.setActive(true);
                                        beacon.setBeaconIdentifier(beaconSighting.getBeacon().getIdentifier());
                                        for (Visit visit : list){
                                            if (visit.getPlace().getName().equals(beaconSighting.getBeacon().getName())){
                                                beacon.setVisitId(visit.getVisitID());
                                            }
                                        }
                                        user.getBeacons().add(beacon);
                                        Map<String,Object> map = firebaseManager.getMapUpdateBeaconHistory(user,user.getBeacons());
                                        firebaseManager.getFirebaseDatabase().getReference().updateChildren(map);
                                    }
                                }
                            }
                            SplashActivity.BeaconId = beaconSighting.getBeacon().getIdentifier();
                            Intent intent = new Intent(result);
                            intent.putExtra("Message", "changeView");
                            broadcaster.sendBroadcast(intent);
                        }
                    }
                }
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
