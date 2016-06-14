package edu.unc.sjyan.locationawareplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.common.api.Status;


import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.content.Intent;
import android.app.PendingIntent;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        ResultCallback<Status>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient c = null;
    private List mGeofenceList = new ArrayList<Geofence>();
    private PendingIntent mGeofencePendingIntent;
    private TextView tv;
    private TextView tv2;
    private TextView tv3;
    private LocationPlot lp;

    // public TransitionResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lp = (LocationPlot) findViewById(R.id.locationPlot);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);

        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;

        IntentFilter filter = new IntentFilter("geofence_transition_action");
        this.registerReceiver(mReceiver, filter);

        populateGeofenceList();
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();

        Intent pause = new Intent(this, TransitionReceiver.class);
        pause.putExtra("isPlaying", true);
        this.sendBroadcast(pause);

    }


    public void onResult(Status status) {
        if(status.isSuccess()) {
            Log.i("Geofence", "Successfully added geofences");
        } else {
            Log.e("Geofence", "Could not add geofence");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        c = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("TAG", "We are connected to Google Services");
        try {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(c);
            Log.v("LOC", "" + loc.getLatitude() + ", " + loc.getLongitude());


            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(c, mLocationRequest, this);

            // Add geofences
            LocationServices.GeofencingApi.addGeofences(
                    c,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);

        }
        catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.v("Pending Intent", " Called getGeofencePendingIntent");
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        // Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        Intent intent = new Intent(this, TransitionReceiver.class);
        intent.setAction("geofence_transition_action");
        // mReceiver.toIntent(intent);
        // intent.putExtra("receiver", mReceiver);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Connection" , " suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Connection Status", connectionResult.toString());
    }

    public void populateGeofenceList() {

        // Test Graham
        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId("graham")
                        .setCircularRegion(
                                35.912938,
                                -79.046768,
                                75f
                                // 10
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(10000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .build()
        );
        // Build Sitterson geofence
        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId("sitterson")
                        .setCircularRegion(
                                35.909860,
                                -79.053247,
                                85f
                                // 48f
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(10000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .build()
        );
        // Old well
        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId("old_well")
                        .setCircularRegion(
                                35.912059,
                                -79.051243,
                                75f
                                // 10
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(10000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .build()
        );
        // Polk Place
        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId("polk")
                        .setCircularRegion(
                                35.910639,
                                -79.050400,
                                85f
                                // 43
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(10000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .build()
        );

        Log.v("Main", "Populated Geofence List");
    }


    @Override
    protected void onStart() {
        c.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        c.disconnect();
        super.onStop();
        Log.v("Stopped", "...");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("onLocationChanged", "Location changed!");
        Log.v("CLASS GPS", location.getLatitude() + ", " + location.getLongitude());

        Geocoder g = new Geocoder(this, Locale.getDefault());
        try {
            Log.v("Reached", "geocoder");
            List<Address> la = g.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = la.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            tv.setText("Address: " + addressFragments.get(0) + ", " + addressFragments.get(1));
            tv2.setText("Latitude: " + location.getLatitude() + " | Longitude: " +
                            location.getLongitude());

            //la = g.getFromLocationName("Sitterson Hall", 1);
            //Log.v("From name", la.get(0).toString());

        } catch (Exception ex) {
            Log.e("onLocationChanged", ex.toString());
        }

        // Intent cIntent = getIntent();
        // lp.feedLocation(cIntent.getStringExtra(GeofenceTransitionsIntentService.));
        // lp.feedLocation(location.getLatitude(), location.getLongitude()); // geofence
        // lp.invalidate();

        /*
        Intent update = getIntent();

        switch (update.getIntExtra("location", -1)) {
            case TransitionReceiver.STATUS_ERROR:
                Toast.makeText(this, "Error resolving geofence transition",
                        Toast.LENGTH_LONG).show();
                break;
            case TransitionReceiver.STATUS_ENTERED_SITTERSON:
                lp.drawSitterson();
                break;
            case TransitionReceiver.STATUS_ENTERED_OLD:
                lp.drawOldWell();
                break;
            case TransitionReceiver.STATUS_ENTERED_POLK:
                lp.drawPolkPlace();
                break;
            case TransitionReceiver.STATUS_EXITED_SITTERSON:
                lp.invalidate();
                break;
            case TransitionReceiver.STATUS_EXITED_OLD:
                lp.invalidate();
                break;
            case TransitionReceiver.STATUS_EXITED_POLK:
                lp.invalidate();
                break;
            default:
                Log.v("Updating intent", update.getIntExtra("location", -1) + "");
        }

        */

    }

    BroadcastReceiver mReceiver =  new WakefulBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();
            String message = b.getString("message");
            Log.e("newmesage", "" + message);

            long resultCode = intent.getIntExtra("location", -1);
            Log.v("Intent result code", resultCode + "");

            switch ((int) resultCode) {
                case TransitionReceiver.STATUS_ERROR:
                    Toast.makeText(context, "Error resolving geofence transition",
                            Toast.LENGTH_LONG).show();
                    break;
                case TransitionReceiver.STATUS_ENTERED_SITTERSON:
                    lp.drawSitterson();
                    lp.invalidate();
                    break;
                case TransitionReceiver.STATUS_ENTERED_OLD:
                    lp.drawOldWell();
                    lp.invalidate();
                    break;
                case TransitionReceiver.STATUS_ENTERED_POLK:
                    lp.drawPolkPlace();
                    lp.invalidate();
                    break;
                case TransitionReceiver.STATUS_EXITED_SITTERSON:
                    lp.clear();
                    lp.invalidate();
                    break;
                case TransitionReceiver.STATUS_EXITED_OLD:
                    lp.clear();
                    lp.invalidate();
                    break;
                case TransitionReceiver.STATUS_EXITED_POLK:
                    lp.clear();
                    lp.invalidate();
                    break;
                default:
                    Log.v("Updating intent AL", intent.getIntExtra("location", -1) + "");
            }

        }
    };

}
