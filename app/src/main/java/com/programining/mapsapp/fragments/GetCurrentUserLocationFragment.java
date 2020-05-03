package com.programining.mapsapp.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.programining.mapsapp.R;
import com.programining.mapsapp.actvities.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetCurrentUserLocationFragment extends Fragment implements OnMapReadyCallback {


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


    private FusedLocationProviderClient mFusedLocationClient;

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
            mMapView.getMapAsync(GetCurrentUserLocationFragment.this);// link map view with OnMapReadyCallback

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

            btnGetLocationInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getLastLocation();
                }
            });

            final ConstraintLayout clInfo = parentView.findViewById(R.id.cl_info);
            FloatingActionButton fabInfo = parentView.findViewById(R.id.fab_info);
            fabInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clInfo.getVisibility() == View.GONE) {
                        clInfo.setVisibility(View.VISIBLE);
                    } else {
                        clInfo.setVisibility(View.GONE);

                    }
                }
            });

        } else {
            requestRuntimePermission();
        }

        return parentView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


// default location
        getLastLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //latLng is the location  that the user clicked

               mLat = latLng.latitude;
                mLng = latLng.longitude;

                getLocationName();


                //   Toast.makeText(mContext, latLng.latitude + "/" + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * get full address of the location!
     */
    private void getLocationName() {
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            //get address name
            List<Address> addresses = geocoder.getFromLocation(mLat, mLng, 1);
            String fullAddress = addresses.get(0).getAddressLine(0);
            setLocationPen(fullAddress);
            tvLocationName.setText(fullAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * get registered location
     */
    private void getLastLocation() {
        if (isLocationEnabled()) {
            //get user last location
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();// get last location

                    if (location == null) {
                        // request new location Data
                        requestNewLocationData();
                    } else {
                        // location is available
                        mLat = location.getLatitude();
                        mLng = location.getLongitude();
                        getLocationName();
                    }


                }
            });

        } else {
            Toast.makeText(mContext, "Please enable GPS", Toast.LENGTH_SHORT).show();

            //take the user to gps enabling screen
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);

        }
    }

    /**
     * Request for location in case no data found
     */
    private void requestNewLocationData() {
        //request new location data

        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(0);
        request.setFastestInterval(0);
        request.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(request, myLocationCallback(), Looper.myLooper());

    }

    /**
     * on new location request get the new location
     *
     * @return LocationCallback
     */
    private LocationCallback myLocationCallback() {

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                mLat = location.getLatitude();
                mLng = location.getLongitude();
                getLocationName();

            }
        };

        return callback;
    }

    /**
     * check if gps or network is/are enabled
     *
     * @return boolean : true if  one at least is enabled (GPS or Network), otherwise return false
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    /**
     * draw location  pen
     *
     * @param fullAddress is required
     */
    private void setLocationPen(String fullAddress) {
        LatLng location = new LatLng(mLat, mLng);

        if (selectedLocation != null) {
            selectedLocation.remove();
        }
        selectedLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(mLat, mLng)).title(fullAddress));// add marker non the location


        //move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location)); //move camera up,bottom, left, right

        //to zoom to selected location
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16f), 1000, null);

        tvLat.setText(mLat + "");
        tvLng.setText(mLng + "");

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

