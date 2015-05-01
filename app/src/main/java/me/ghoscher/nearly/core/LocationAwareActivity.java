package me.ghoscher.nearly.core;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import me.ghoscher.nearly.fragments.GooglePlayErrorDialog;

/**
 * Created by Hisham on 29/4/2015.
 */

/**
 * Activity handles the life cycle of the {@link com.google.android.gms.common.api.GoogleApiClient} and registers for
 * location updates
 */
public abstract class LocationAwareActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GooglePlayErrorDialog.GooglePlayErrorDialogHost {

    private static final int REQ_RESOLVE_CLIENT_ERROR = 1001; // Keep low codes for sub classes

    // State keys
    private static final String KEY_RESOLVING_ERROR = "resolving_client_error";
    private static final String KEY_LOCATION_UPDATES = "request_location_updates";

    // Google client, main purpose of this class
    private GoogleApiClient googleApiClient;

    // Are we currently resolving Play Services error?
    private boolean resolvingClientError = false;
    // Are we currently requesting location updates?
    private boolean requestingLocationUpdates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create client and request location services
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Restore state flags
        if (savedInstanceState != null) {
            resolvingClientError = savedInstanceState.getBoolean(KEY_RESOLVING_ERROR, false);
            requestingLocationUpdates = savedInstanceState.getBoolean(KEY_LOCATION_UPDATES, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save states flags
        outState.putBoolean(KEY_RESOLVING_ERROR, resolvingClientError);
        outState.putBoolean(KEY_LOCATION_UPDATES, requestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Try connecting
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect
        if (googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    /**
     * Starts the request for location updates
     */
    protected void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setSmallestDisplacement(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                internalLocationListener
        );

        requestingLocationUpdates = true;
    }

    /**
     * Stops requesting location updates
     */
    protected void stopLocationUpdates() {
        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, internalLocationListener);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Client connected
        startLocationUpdates();
    }

    // Receive location updates and do any filtering of any kind before passing it to child class
    private LocationListener internalLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            onLocationUpdate(location);
        }
    };

    /**
     * Receives location updates as long as requests are running
     * @param location
     */
    protected abstract void onLocationUpdate(Location location);

    @Override
    protected void onPause() {
        stopLocationUpdates();
        requestingLocationUpdates = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleApiClient.isConnected() && !requestingLocationUpdates)
            startLocationUpdates();
    }

    /**
     * Subclasses must implement this and handle connection drops
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (resolvingClientError) {
            // Already doing something about it
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                resolvingClientError = true;
                connectionResult.startResolutionForResult(this, REQ_RESOLVE_CLIENT_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            resolvingClientError = true;
            GooglePlayErrorDialog.newInstance(connectionResult.getErrorCode(), REQ_RESOLVE_CLIENT_ERROR);
        }
    }

    public void onErrorDialogCancel(GooglePlayErrorDialog dlg, int errorCode) {
        // User doesn't want it to be fixed
        resolvingClientError = false;
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle fix result
        if (requestCode == REQ_RESOLVE_CLIENT_ERROR) {
            resolvingClientError = false;

            if (resultCode == RESULT_OK && !googleApiClient.isConnected() && !googleApiClient.isConnecting())
                googleApiClient.connect();
        }
    }
}
