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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.yuyu.module.R;

import java.util.ArrayList;

import rx.Observable;

public class MapFragment_ extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private final String TAG = MapFragment_.class.getSimpleName();
    private final int INDEX = 3;

    private static View view;
    private final int GPS_REQUEST_CODE = 6888;

    private Context context;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private boolean isGPS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = getActivity();
        getActivity().setTitle(getString(R.string.nav_map));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermission();
    }

    public void requestPermission() {
        new TedPermission(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.e(TAG, "권한 허가");
                        initialize();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Log.e(TAG, "권한 거부");
                        Toast.makeText(context, getString(R.string.permission_request), Toast.LENGTH_SHORT).show();
                        Observable.just(view)
                                .compose(RxLifecycleAndroid.bindView(view))
                                .filter(view1 -> view1 != null)
                                .map(view2 -> (ViewGroup) view2.getParent())
                                .filter(viewGroup1 -> viewGroup1 != null)
                                .subscribe(viewGroup2 -> viewGroup2.removeView(view));
                    }
                })
                .setDeniedMessage(getString(R.string.permission_denied))
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    public void initialize() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        final int CAMERA_DEFAULT = 10;
        final LatLng SEOUL = new LatLng(37.56, 126.97);
        Observable.just(googleMap = map)
                .compose(RxLifecycleAndroid.bindView(view))
                .subscribe(googleMap1 -> {
                    googleMap1.clear();
                    googleMap1.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                    googleMap1.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_DEFAULT));
                    googleMap1.setOnMapLoadedCallback(this::checkGPS);
                });
    }

    public void checkGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialDialog.Builder(context)
                    .content(getString(R.string.gps_alert))
                    .cancelable(false)
                    .positiveText(getString(R.string.alert_yes))
                    .negativeText(getString(R.string.alert_no))
                    .onPositive((dialog, which) -> startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE))
                    .onNegative((dialog, which) -> dialog.cancel())
                    .show();
        } else {
            Observable.just(googleApiClient = buildApi())
                    .compose(RxLifecycleAndroid.bindView(view))
                    .subscribe(GoogleApiClient::connect);
        }
    }

    public GoogleApiClient buildApi() {
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable.just(requestCode)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(o -> requestCode == GPS_REQUEST_CODE && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                .subscribe(o -> {
                    isGPS = true;
                    mapFragment.getMapAsync(this);
                    Toast.makeText(context, getString(R.string.gps_load), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onConnected(Bundle bundle) {
        final int INTERVAL = 1000;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(INTERVAL)
                        .setFastestInterval(INTERVAL), this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        final int CAMERA_ZOOM = 15;
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

}
