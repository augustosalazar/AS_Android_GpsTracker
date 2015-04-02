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
    private float mMapZoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }

    private void startGPS() {
        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        Location location =  mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));

        //Criteria criteria = new Criteria();
        //bestProvider = mLocationManager.getBestProvider(criteria, false);

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
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
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
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
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
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 1, this);

        } else {
            mLocationManager.removeUpdates(this);
        }
    }
}
