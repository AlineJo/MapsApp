package com.programining.mapsapp.actvities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.programining.mapsapp.R;

public class DisplayLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int KEY_PERMISSION_REQUEST_ID = 100;
    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isPermissionGranted()) {
            setContentView(R.layout.activity_dislplay_location_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            requestPermission();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera -34,151

        LatLng sydney = new LatLng(23.5673,  58.1725);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng secondLocation = new LatLng(23,  58);
        mMap.addMarker(new MarkerOptions().position(secondLocation).title("Marker in Oman"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(secondLocation));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    /**
     * check if permission is granted
     */
    private boolean isPermissionGranted() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    /**
     * request run-time-permission to access location
     */
    private void requestPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(permissions, KEY_PERMISSION_REQUEST_ID);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == KEY_PERMISSION_REQUEST_ID) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //user granted the permission

                //refresh Map Activity
                Intent i = new Intent(DisplayLocationMapsActivity.this, DisplayLocationMapsActivity.class);
                startActivity(i);
                finish();// close current screen

            } else {
                // user didn't grant the permission
                finish();// close current screen
            }
        }

    }
}
