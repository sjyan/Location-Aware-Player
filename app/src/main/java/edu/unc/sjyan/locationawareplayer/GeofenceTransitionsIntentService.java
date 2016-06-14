package edu.unc.sjyan.locationawareplayer;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.app.IntentService;
import android.content.Intent;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.view.View;
import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;
import android.os.Handler;
import android.app.Activity;

/**
 * Created by Stephen on 3/24/16.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    public static final int STATUS_ERROR = 0;
    public static final int STATUS_ENTERED_SITTERSON = 1;
    public static final int STATUS_ENTERED_OLD = 2;
    public static final int STATUS_ENTERED_POLK = 3;
    public static final int STATUS_EXITED_SITTERSON = 4;
    public static final int STATUS_EXITED_OLD = 5;
    public static final int STATUS_EXITED_POLK = 6;



    private Handler tHandler;
    private MediaPlayer mpS;
    private MediaPlayer mpO;
    private MediaPlayer mpP;

    /*
    mediaPlayer = MediaPlayer.create(applicationContext, R.raw)
            mediaPlayer.start()
            if != null AND isPlaying
    mediaPlayer.pause()
    */

    protected static final String TAG = "GeofenceTransitionsIS";
    private TextView tv3;

    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
        Log.v(TAG, "created intent service");
        tHandler = new Handler();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mpS = MediaPlayer.create(getApplicationContext(), R.raw.coolkids);
        mpO = MediaPlayer.create(getApplicationContext(), R.raw.sugar);
        mpP = MediaPlayer.create(getApplicationContext(), R.raw.sunshine);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // intent.removeCategory("receiver");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        ResultReceiver receiver = new TransitionResultReceiver(new Handler()).fromIntent(intent);
        // ResultReceiver receiver = intent.getParcelableExtra("receiver");
        // Bundle bundle = new Bundle();

        if (geofencingEvent.hasError()) {
            Log.e("Geofencing Error", "...");
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
            return;
        }


        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.v(TAG, "entered geofence");
        } else {
            Log.v(TAG, "no transition yet...");
        }

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        if(triggeringGeofences != null) {
            String[] triggerIds = new String[triggeringGeofences.size()];

            for (int i = 0; i < triggerIds.length; i++) {
                triggerIds[i] = triggeringGeofences.get(i).getRequestId();
            }

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                for (int i = 0; i < triggerIds.length; i++) {
                    if (triggerIds[i].equals("sitterson")) {

                        tHandler.post(new DisplayToast(this, "Entered Sitterson"));
                        if(!mpS.isPlaying()) mpS.start();
                        receiver.send(STATUS_ENTERED_SITTERSON, Bundle.EMPTY);
                        Log.v(TAG, "Entered Sitterson and playing music...");
                    } else if (triggerIds[i].equals("old_well")) {
                        tHandler.post(new DisplayToast(this, "Entered Old Well"));
                        if(!mpO.isPlaying()) mpO.start();
                    } else if (triggerIds[i].equals("polk")) {
                        tHandler.post(new DisplayToast(this, "Entered Polk Place"));
                        if(!mpP.isPlaying()) mpP.start();
                    }
                }
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                for (int i = 0; i < triggerIds.length; i++) {
                    tHandler.post(new DisplayToast(this, "Left Sitterson"));
                    if(triggerIds[i].equals("sitterson")) {
                        receiver.send(STATUS_EXITED_SITTERSON, Bundle.EMPTY);
                        if (mpS != null && mpS.isPlaying()) {
                            mpS.stop();
                        }
                    } else if (triggerIds[i].equals("old_well")) {
                        tHandler.post(new DisplayToast(this, "Left Old Well"));
                        if (mpO != null && mpO.isPlaying()) {
                            mpO.stop();
                        }
                    } else if (triggerIds[i].equals("polk")) {
                        tHandler.post(new DisplayToast(this, "Left Polk Place"));
                        if (mpP != null && mpP.isPlaying()) {
                            mpP.stop();
                        }
                    }
                }
            } else {
                receiver.send(STATUS_ERROR, Bundle.EMPTY);
                Log.e(TAG, "Invalid geofence transition type");
            }
        }

    }

    private void updateLocationText(String notificationDetails) {
        Log.v(TAG, "Updating Location Text");
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        //Toast.makeText(getApplicationContext(), notificationDetails, Toast.LENGTH_SHORT).show();


    }

}
