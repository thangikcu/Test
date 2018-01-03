package com.example.thanggun99.test2.one;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by thanggun99 on 8/28/17.
 */

public class MapManager implements LocationListener{
    private static final String TAG = "MapManager";

    private Context context;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private MarkerOptions myMarkerOptions;
    private Marker myMarker;
    private double latitude;
    private double longitude;

    public MapManager(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setup() {
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        googleMap.setOnMyLocationButtonClickListener(() -> {
            Log.d(TAG, "onMyLocationButtonClick()...");
            if (isEnableGPS()) {
                return false;
            } else {
                openGPSSetting();
                return true;
            }
        });
    }

    public void addMyMarker() {
        try {
            Criteria criteria = new Criteria();
            criteria.setPowerRequirement(Criteria.ACCURACY_MEDIUM);

            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            myMarkerOptions = new MarkerOptions();
            myMarkerOptions.title("PhongBM");
            myMarkerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            myMarkerOptions.position(new LatLng(latitude, longitude));
            myMarker = googleMap.addMarker(myMarkerOptions);

            locationManager.requestLocationUpdates(1000, 2, criteria, this, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void drawShapes() {
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(latitude, longitude))
                .add(new LatLng(latitude - 0.10, longitude))
                .add(new LatLng(latitude - 0.10, longitude - 0.2))
                .add(new LatLng(latitude, longitude - 0.2))
                .add(new LatLng(latitude, longitude));
        Polyline polyline = googleMap.addPolyline(rectOptions);

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(1000)
                .strokeWidth(10)
                .strokeColor(Color.RED)
                .fillColor(Color.GREEN);
        Circle circle = googleMap.addCircle(circleOptions);
    }

    private void openGPSSetting() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Open GPS")
                .setMessage("Do you want to open GPS?")
                .setPositiveButton("Open setting", (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();
    }

    private boolean isEnableGPS() {
        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        myMarker.setPosition(latLng);

        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.target(latLng);
        builder.zoom(15); // 1 - 5 - 10 - 15 - 20
        CameraPosition cameraPosition = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
