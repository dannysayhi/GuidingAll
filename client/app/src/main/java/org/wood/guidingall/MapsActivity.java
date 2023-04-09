package org.wood.guidingall;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import org.wood.guidingall.constructor.MyLocation;
import org.wood.guidingall.constructor.MyMap;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mGoogleMap; // Might be null if Google Play services APK is not available.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        MyMap mMap = new MyMap(mGoogleMap);
        MyLocation mLocation = new MyLocation(MapsActivity.this);
        LatLng mLatLng = mLocation.getLatLng();

        mLocation.init();
        mMap.cameraFocusOnLocation(mLatLng.latitude, mLatLng.longitude, 15);
        mMap.onMyLocationChangeEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mGoogleMap == null) {
            mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mGoogleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mGoogleMap.clear();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
    }
}
