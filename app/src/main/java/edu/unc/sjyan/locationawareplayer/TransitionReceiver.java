package edu.unc.sjyan.locationawareplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import java.util.List;

/**
 * Created by Stephen on 3/29/16.
 */
public class TransitionReceiver extends WakefulBroadcastReceiver {

    private Context context;
    public static final String TAG = TransitionReceiver.class.getSimpleName();
    private MediaPlayer mp;
    private MediaPlayer mpS;
    private MediaPlayer mpO;
    private MediaPlayer mpP;

    public static final int STATUS_ERROR = 0;
    public static final int STATUS_ENTERED_SITTERSON = 1;
    public static final int STATUS_ENTERED_OLD = 2;
    public static final int STATUS_ENTERED_POLK = 3;
    public static final int STATUS_EXITED_SITTERSON = 4;
    public static final int STATUS_EXITED_OLD = 5;
    public static final int STATUS_EXITED_POLK = 6;

    public TransitionReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(intent.getBooleanExtra("isPlaying", false)) {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                Log.v(TAG, "paused player because exited application");
            }
        }

        if (geofencingEvent.hasError()) {

            Log.e("Geofencing Error", "...");
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
                        Toast.makeText(context, "Entered Sitterson", Toast.LENGTH_SHORT).show();
                        Intent update = new Intent("geofence_transition_action");
                        update.putExtra("location", STATUS_ENTERED_SITTERSON);
                        update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(update);

                        mp = MediaPlayer.create(context, R.raw.coolkids);
                        mp.start();

                    } else if (triggerIds[i].equals("old_well")) {
                        Toast.makeText(context, "Entered Old Well", Toast.LENGTH_SHORT).show();
                        Intent update = new Intent("geofence_transition_action");
                        update.putExtra("location", STATUS_ENTERED_OLD);
                        update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(update);


                        mp = MediaPlayer.create(context, R.raw.sugar);
                        mp.start();

                    } else if (triggerIds[i].equals("polk")) {
                        Toast.makeText(context, "Entered Polk Place", Toast.LENGTH_SHORT).show();
                        Intent update = new Intent("geofence_transition_action");
                        update.putExtra("location", STATUS_ENTERED_POLK);
                        update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(update);

                        mp = MediaPlayer.create(context, R.raw.sunshine);
                        mp.start();
                    }
                }
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                for (int i = 0; i < triggerIds.length; i++) {
                    // tHandler.post(new DisplayToast(this, "Left Sitterson"));
                    if(triggerIds[i].equals("sitterson")) {
                        Toast.makeText(context, "Leaving Sitterson", Toast.LENGTH_SHORT).show();
                        Intent update = new Intent("geofence_transition_action");
                        update.putExtra("location", STATUS_EXITED_SITTERSON);
                        update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(update);

                        mp.stop();

                    } else if (triggerIds[i].equals("old_well")) {
                        Toast.makeText(context, "Leaving Old Well", Toast.LENGTH_SHORT).show();
                        Intent update = new Intent("geofence_transition_action");
                        update.putExtra("location", STATUS_EXITED_OLD);
                        update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(update);

                            mp.stop();
                    } else if (triggerIds[i].equals("polk")) {
                        Toast.makeText(context, "Leaving Polk Place", Toast.LENGTH_SHORT).show();
                        Intent update = new Intent("geofence_transition_action");
                        update.putExtra("location", STATUS_EXITED_POLK);
                        update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(update);

                            mp.stop();

                    }
                }
            } else {
                Log.e(TAG, "Invalid geofence transition type");
            }
        }
    }

}
