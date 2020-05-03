package com.programining.mapsapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.programining.mapsapp.R;
import com.programining.mapsapp.actvities.MainActivity;


public class DisplayLocationFragment extends Fragment implements OnMapReadyCallback {
    private static final int KEY_PERMISSION_REQUEST_ID = 100;
    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;
    View parentView;

    public DisplayLocationFragment() {

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        parentView = inflater.inflate(R.layout.fragment_display_location, container, false);

        if(isPermissionGranted()) {  // in order to display map in fragment
            mMapView = parentView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();// display map ASAP
            MapsInitializer.initialize(mContext);// initialize map
            mMapView.getMapAsync(DisplayLocationFragment.this);// link map view with OnMapReadyCallback
        }
        else{
            requestRuntimePermission();
        }
        return parentView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera -34,151

        LatLng sydney = new LatLng(23.5673, 58.1725);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker1 From Fragment"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng secondLocation = new LatLng(23, 58);
        mMap.addMarker(new MarkerOptions().position(secondLocation).title("Marker2 From Fragment"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(secondLocation));
    }


    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRuntimePermission() {
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(), permissions, KEY_PERMISSION_REQUEST_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == KEY_PERMISSION_REQUEST_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //user granted the permission
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            } else {
                // user didn't grant the permission
                getActivity().finish(); // close the host activity.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
