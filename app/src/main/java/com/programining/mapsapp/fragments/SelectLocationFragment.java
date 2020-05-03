package com.programining.mapsapp.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.programining.mapsapp.R;
import com.programining.mapsapp.actvities.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectLocationFragment extends Fragment implements OnMapReadyCallback {



    private static final int KEY_PERMISSION_REQUEST_ID = 200;
    private Context mContext;
    private MapView mMapView;
    private GoogleMap mMap;

    private TextView tvLocationName;
    private TextView tvLat;
    private TextView tvLng;
    private Button btnGetLocationInfo;
    private Marker selectedLocation;

    private double mLat;
    private double mLng;

    //get address name
    private List<Address> addresses;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_select_loction, container, false);

        tvLocationName = parentView.findViewById(R.id.tv_place);
        tvLat = parentView.findViewById(R.id.tv_lat);
        tvLng = parentView.findViewById(R.id.tv_lng);
        btnGetLocationInfo = parentView.findViewById(R.id.btn_getCurrentLocation);


        if (isPermissionGranted()) {  // in order to display map in fragment
            mMapView = parentView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();// display map ASAP
            MapsInitializer.initialize(mContext);// initialize map
            mMapView.getMapAsync(SelectLocationFragment.this);// link map view with OnMapReadyCallback

        } else {
            requestRuntimePermission();
        }

        return parentView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


// default location
        LatLng KOM4 = new LatLng(23.5673, 58.1725);
        mLat = KOM4.latitude;
        mLng = KOM4.longitude;
        tvLat.setText(mLat + "");
        tvLng.setText(mLng + "");
        selectedLocation = mMap.addMarker(new MarkerOptions().position(KOM4).title("KOM 4"));// add marker non the location
        tvLocationName.setText("KOM4");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //latLng is the location  that the user clicked

                if (selectedLocation != null) {
                    selectedLocation.remove();
                }
                mLat = latLng.latitude;
                mLng = latLng.longitude;
                tvLat.setText(mLat + "");
                tvLng.setText(mLng + "");

                getLocationName();

                //   Toast.makeText(mContext, latLng.latitude + "/" + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getLocationName() {
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            addresses = geocoder.getFromLocation(mLat, mLng, 1);
            String fullAddress = addresses.get(0).getAddressLine(0);
            setLocationPen(fullAddress);
            tvLocationName.setText(fullAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void setLocationPen(String fullAddress) {
        selectedLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(mLat, mLng)).title(fullAddress));// add marker non the location
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
