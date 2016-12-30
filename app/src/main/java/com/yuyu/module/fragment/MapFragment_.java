package com.yuyu.module.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yuyu.module.R;

public class MapFragment_ extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private final String TAG = MapFragment_.class.getSimpleName();

    private static View view;
    private final int REQUEST_CODE = 6888;

    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private boolean isGPS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException ignored) {
        }
        getActivity().setTitle(getString(R.string.nav_map));
        initialize();
        return view;
    }

    public void initialize() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e(TAG, "MapReady");
        googleMap = map;
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.56, 126.97)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        googleMap.setOnMapLoadedCallback(this::check);
    }

    public void check() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialDialog.Builder(getActivity())
                    .content(getString(R.string.gps_alert))
                    .cancelable(false)
                    .positiveText(getString(R.string.alert_yes))
                    .negativeText(getString(R.string.alert_no))
                    .onPositive((dialog, which) -> startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE))
                    .onNegative((dialog, which) -> dialog.cancel())
                    .show();
        } else {
            if (googleApiClient == null) {
                Log.e(TAG, "null");
                onConnect();
            } else {
                Log.e(TAG, "not null");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    isGPS = true;
                    mapFragment.getMapAsync(this);
                }
                break;
        }
    }

    public void onConnect() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Connected");
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            return;
        }
        Log.e(TAG, location.getLatitude() + " / " + location.getLongitude());
        makeMaker(location);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isGPS) {
            makeMaker(location);
            isGPS = false;
        }
    }

    public void makeMaker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("현재위치");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
    }

}
