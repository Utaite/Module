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
import android.support.v4.app.ActivityCompat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.yuyu.module.R;

import rx.Observable;

public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private final String TAG = MapFragment.class.getSimpleName();

    private static View view;
    private final int REQUEST_CODE = 6888, CAMERA_ZOOM = 15;

    private GoogleMap googleMap;
    private com.google.android.gms.maps.MapFragment mapFragment;
    private GoogleApiClient googleApiClient;
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
        mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        final LatLng SEOUL = new LatLng(37.56, 126.97);
        Observable.just(googleMap)
                .compose(RxLifecycleAndroid.bindView(view))
                .map(googleMap1 -> googleMap1 = map)
                .subscribe(googleMap2 -> {
                    googleMap2.clear();
                    googleMap2.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                    googleMap2.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_ZOOM));
                    googleMap2.setOnMapLoadedCallback(this::checkGPS);
                });
    }

    public void checkGPS() {
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
            onConnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable.empty()
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(o -> requestCode == REQUEST_CODE && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                .subscribe(o -> {
                    isGPS = true;
                    mapFragment.getMapAsync(this);
                    Toast.makeText(getActivity(), getString(R.string.gps_load), Toast.LENGTH_LONG).show();
                });
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
        final int INTERVAL = 1000;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(INTERVAL)
                        .setFastestInterval(INTERVAL), this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            makeMaker(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Observable.just(location)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(location1 -> isGPS)
                .subscribe(location1 -> {
                    isGPS = false;
                    makeMaker(location);
                });
    }

    public void makeMaker(Location location) {
        Observable.just(location)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(location1 -> location1 != null)
                .map(location2 -> new LatLng(location2.getLatitude(), location2.getLongitude()))
                .subscribe(latLng1 -> {
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng1)
                            .title(getString(R.string.gps_locate)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_ZOOM));
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Observable.just(view)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(view1 -> view1 != null)
                .map(view2 -> (ViewGroup) view2.getParent())
                .filter(viewGroup1 -> viewGroup1 != null)
                .subscribe(viewGroup2 -> viewGroup2.removeView(view));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

}
