package com.yuyu.module.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.MapVO;

import java.util.ArrayList;

import rx.Observable;

public class MapFragment_ extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private final String TAG = MapFragment_.class.getSimpleName();

    private static View view;
    private final int GPS_REQUEST_CODE = 6888;

    private Context context;
    private GoogleMap googleMap;
    private Marker selectedMarker;
    private MapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private boolean isGPS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = getActivity();
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
                        initialize();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
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
                .doOnSubscribe(() -> googleMap.clear())
                .doOnUnsubscribe(() -> {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_DEFAULT));
                })
                .subscribe(googleMap1 -> {
                    googleMap1.setOnMarkerClickListener(this);
                    googleMap1.setOnMapClickListener(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable.just(requestCode)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(o -> requestCode == GPS_REQUEST_CODE && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                .subscribe(o -> {
                    isGPS = true;
                    mapFragment.getMapAsync(this);
                    ((MainActivity) getActivity()).getToast().setText(getString(R.string.gps_load));
                    ((MainActivity) getActivity()).getToast().show();
                });
    }

    public GoogleApiClient buildApi() {
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        final int INTERVAL = 1000;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(INTERVAL)
                        .setFastestInterval(INTERVAL), this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            prepareMarker(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Observable.just(location)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(location1 -> isGPS)
                .subscribe(location1 -> {
                    isGPS = false;
                    prepareMarker(location);
                });
    }

    public void prepareMarker(Location location) {
        final int CAMERA_ZOOM = 15;
        Observable.just(location)
                .compose(RxLifecycleAndroid.bindView(view))
                .filter(location1 -> location1 != null)
                .map(location2 -> new MapVO(location2.getLatitude(), location2.getLongitude(), getString(R.string.gps_locate)))
                .doOnUnsubscribe(() -> {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_ZOOM));
                })
                .subscribe(vo -> createMarker(vo, false));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        selectedMarkerRemove();
        nonMarkerRemove(marker);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        selectedMarkerRemove();
    }

    public void selectedMarkerRemove() {
        Observable.just(selectedMarker)
                .compose(RxLifecycleAndroid.bindView(view))
                .subscribe(marker1 -> createMarker(marker1, false));
    }

    public void nonMarkerRemove(Marker marker) {
        Observable.just(marker)
                .compose(RxLifecycleAndroid.bindView(view))
                .subscribe(marker1 -> selectedMarker = createMarker(marker1, true));
    }

    public Marker createMarker(Marker marker, boolean isSelected) {
        if (marker != null) {
            marker.remove();
            return createMarker(new MapVO(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle()), isSelected);
        }
        return null;
    }

    public Marker createMarker(MapVO vo, boolean isSelected) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null);
        TextView tv_marker = (TextView) markerView.findViewById(R.id.tv_marker);
        tv_marker.setText(vo.getDescription());
        tv_marker.setBackgroundResource(isSelected ? R.drawable.ic_marker_phone_blue : R.drawable.ic_marker_phone);
        tv_marker.setTextColor(isSelected ? Color.WHITE : Color.BLACK);
        return googleMap.addMarker(new MarkerOptions().title(vo.getDescription())
                .position(new LatLng(vo.getLat(), vo.getLon()))
                .icon(BitmapDescriptorFactory.fromBitmap(createBitmap(context, markerView))));
    }

    public Bitmap createBitmap(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Observable.just(view)
                .compose(RxLifecycleAndroid.bindView(view))
                .doOnSubscribe(() -> {
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
                    view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                })
                .subscribe(View::buildDrawingCache);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
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
