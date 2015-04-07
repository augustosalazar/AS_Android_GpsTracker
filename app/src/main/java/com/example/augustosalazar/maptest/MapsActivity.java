package com.example.augustosalazar.maptest;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;
    private boolean mStayWithMap;
    private boolean mAutoMark;
    private boolean mGpsStarted;
    private String bestProvider;
    private float mMapZoom = 17.0f;
    private int mMinDistance = 10;
    private int mMinTime = 0;
    private Criteria mCriteria;
    static final String STATE_STAY = "mStayWithMap";
    static final String STATE_MARK = "mAutoMark";
    static final String STATE_GPS_STATE = "mGpsStarted";
    private ToggleButton mButtonGps;
    private ToggleButton mButtonStay;
    private ToggleButton mButtonMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);

        mButtonGps = (ToggleButton) findViewById(R.id.toggleButtonGps);
        mButtonStay = (ToggleButton) findViewById(R.id.toggleButtonStay);
        mButtonMark = (ToggleButton) findViewById(R.id.toggleButtonMark);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mStayWithMap = savedInstanceState.getBoolean(STATE_STAY);
            mAutoMark = savedInstanceState.getBoolean(STATE_MARK);
            mGpsStarted = savedInstanceState.getBoolean(STATE_GPS_STATE);

        } else {
            setUpMapIfNeeded();

            mStayWithMap = true;
            mAutoMark = true;
            mGpsStarted = false;
        }

        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        mButtonGps.setChecked(mGpsStarted);
        mButtonStay.setChecked(mStayWithMap);
        mButtonMark.setChecked(mAutoMark);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_STAY,mStayWithMap);
        savedInstanceState.putBoolean(STATE_MARK, mAutoMark);
        savedInstanceState.putBoolean(STATE_GPS_STATE, mGpsStarted);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationManager != null)
             mLocationManager.removeUpdates(this);
    }

    private void getLastLocation() {

        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (mCriteria  != null && mLocationManager != null ) {
            bestProvider = mLocationManager.getBestProvider(mCriteria, false);

            Location location = mLocationManager.getLastKnownLocation(bestProvider);

            Toast.makeText(this, "bestProvider is " + bestProvider, Toast.LENGTH_SHORT).show();

            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
            } else {
                Toast.makeText(this, "LastKnownLocation is null", Toast.LENGTH_SHORT).show();
            }
        }

    }




    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                getLastLocation();
            }
        } else {
            getLastLocation();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(this.getClass().getSimpleName(), "Nueva loc " + location.getLatitude() + " " + location.getLongitude());
        if (mAutoMark)
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker"));
        if (mStayWithMap)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( mMapZoom ) );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void onClickButtonStayButton(View view) {
        mStayWithMap = !mStayWithMap;
    }

    public void onClickButtonAutoMark(View view) {
        mAutoMark = !mAutoMark;
    }

    public void onClickButtonGPS(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {

            mGpsStarted = true;
            bestProvider = mLocationManager.getBestProvider(mCriteria, false);
            Toast.makeText(this,"bestProvider is "+bestProvider,Toast.LENGTH_SHORT).show();
            mLocationManager.requestLocationUpdates(bestProvider,
                    mMinTime, mMinDistance
                    , this);
        } else {
            mLocationManager.removeUpdates(this);
            mGpsStarted = false;
        }
    }
}
