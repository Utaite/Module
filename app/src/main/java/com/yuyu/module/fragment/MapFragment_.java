package com.yuyu.module.fragment;

import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.Nullable;
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
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.Constant;
import com.yuyu.module.utils.MapVO;

import java.util.ArrayList;

import rx.Observable;

public class MapFragment_ extends RxFragment implements OnMapReadyCallback {

    private final String TAG = MapFragment_.class.getSimpleName();

    private static View view;

    private Context context;
    private GoogleMap googleMap;
    private Marker selectedMarker;
    private MapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;

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
                        removeGoogleMap();
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
                .compose(bindToLifecycle())
                .doOnSubscribe(() -> googleMap.clear())
                .doOnUnsubscribe(() -> {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_DEFAULT));
                })
                .subscribe(googleMap1 -> {
                    googleMap1.setOnMarkerClickListener(marker -> {
                        Observable.just(selectedMarker)
                                .compose(bindToLifecycle())
                                .filter(marker1 -> marker1 != null)
                                .map(marker1 -> marker1.getPosition().toString())
                                .filter(s -> !s.equals(marker.getPosition().toString()))
                                .doOnUnsubscribe(() -> nonMarkerRemove(marker))
                                .subscribe(s -> selectedMarkerRemove());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        return true;
                    });

                    googleMap1.setOnMapClickListener(latLng -> new MaterialDialog.Builder(context)
                            .content(getString(R.string.map_gps_alert_content))
                            .input(null, getString(R.string.map_gps_alert_input), (dialog, input) -> {
                            })
                            .positiveText(getString(R.string.map_alert_yes))
                            .negativeText(getString(R.string.map_alert_no))
                            .onPositive((dialog, which) -> {
                                selectedMarkerRemove();
                                selectedMarker = createMarker(new MapVO(latLng.latitude, latLng.longitude, dialog.getInputEditText().getText().toString().trim()), true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLng((selectedMarker.getPosition())));
                            })
                            .onNegative((dialog, which) -> dialog.cancel())
                            .show());

                    googleMap1.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            Observable.just(selectedMarker)
                                    .compose(bindToLifecycle())
                                    .filter(marker1 -> marker1 != null)
                                    .map(marker1 -> marker1.getPosition().toString())
                                    .filter(s -> s.equals(marker.getPosition().toString()))
                                    .doOnUnsubscribe(marker::remove)
                                    .subscribe(s -> selectedMarker = null);
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                        }
                    });
                    googleMap1.setOnMapLoadedCallback(this::checkGPS);
                });
    }

    public void checkGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialDialog.Builder(context)
                    .content(getString(R.string.map_gps_alert))
                    .positiveText(getString(R.string.map_alert_setting))
                    .negativeText(getString(R.string.map_alert_no))
                    .onPositive((dialog, which) -> startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constant.GPS_REQUEST_CODE))
                    .onNegative((dialog, which) -> dialog.cancel())
                    .show();
        } else {
            Observable.just(googleApiClient = buildGoogleApiClient())
                    .compose(bindToLifecycle())
                    .subscribe(GoogleApiClient::connect);
        }
    }

    public GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        final int INTERVAL = 1000;
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                                new LocationRequest()
                                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                                        .setInterval(INTERVAL)
                                        .setFastestInterval(INTERVAL), location -> {
                                });
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            prepareMarker(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {
                })
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GPS_REQUEST_CODE && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mapFragment.getMapAsync(this);
            ((MainActivity) getActivity()).getToast().setTextShow(getString(R.string.map_gps_load));
        }
    }

    public void prepareMarker(Location location) {
        final int CAMERA_ZOOM = 15;
        Observable.just(location)
                .compose(bindToLifecycle())
                .filter(location1 -> location1 != null)
                .map(location2 -> new MapVO(location2.getLatitude(), location2.getLongitude(), getString(R.string.map_gps_locate)))
                .doOnUnsubscribe(() -> {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_ZOOM));
                })
                .subscribe(vo -> createMarker(vo, false));
    }

    public void selectedMarkerRemove() {
        Observable.just(selectedMarker)
                .compose(bindToLifecycle())
                .subscribe(marker1 -> createMarker(marker1, false));
    }

    public void nonMarkerRemove(Marker marker) {
        Observable.just(marker)
                .compose(bindToLifecycle())
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
        tv_marker.setBackgroundResource(isSelected ?
                R.drawable.ic_marker_phone_blue : R.drawable.ic_marker_phone);
        tv_marker.setTextColor(isSelected ?
                Color.WHITE : Color.BLACK);

        return googleMap.addMarker(new MarkerOptions()
                .title(vo.getDescription())
                .position(new LatLng(vo.getLat(), vo.getLon()))
                .icon(BitmapDescriptorFactory.fromBitmap(createBitmap(markerView)))
                .draggable(true));
    }

    public Bitmap createBitmap(View markerView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        markerView.draw(new Canvas(bitmap));
        return bitmap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeGoogleMap();
    }

    public void removeGoogleMap() {
        Observable.just(view)
                .compose(bindToLifecycle())
                .filter(view1 -> view1 != null)
                .map(view2 -> (ViewGroup) view2.getParent())
                .filter(viewGroup1 -> viewGroup1 != null)
                .subscribe(viewGroup2 -> viewGroup2.removeView(view));
    }

}