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
    private String bestProvider;
    private float mMapZoom = 17.0f;
    private Criteria mCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);

        setUpMapIfNeeded();



        mStayWithMap = true;
        mAutoMark = true;


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }


    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }

    private void startGPS() {
        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);


        bestProvider = mLocationManager.getBestProvider(mCriteria, false);

        Location location =  mLocationManager.getLastKnownLocation(bestProvider);

        Toast.makeText(this,"bestProvider is "+bestProvider,Toast.LENGTH_SHORT).show();

        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
        } else {
            Toast.makeText(this,"LastKnownLocation is null",Toast.LENGTH_SHORT).show();
        }

        //mLocationManager.requestLocationUpdates(bestProvider,
        //        0, 1, this);

    }




    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                startGPS();

            }
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
        boolean on = ((ToggleButton) view).isChecked();

        mAutoMark = !mAutoMark;
    }

    public void onClickButtonGPS(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {

            bestProvider = mLocationManager.getBestProvider(mCriteria, false);
            Toast.makeText(this,"bestProvider is "+bestProvider,Toast.LENGTH_SHORT).show();
            mLocationManager.requestLocationUpdates(bestProvider,
                    0, 10
                    , this);


        } else {
            mLocationManager.removeUpdates(this);
        }
    }
}
