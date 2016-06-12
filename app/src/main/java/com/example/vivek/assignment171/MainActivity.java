package com.example.vivek.assignment171;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    int MY_PERMISSIONS_REQUEST;
    private GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LatLng loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment));
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap=googleMap;
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(arg0);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
                Marker marker = googleMap.addMarker(markerOptions);
                marker.setInfoWindowAnchor(10,10);
                marker.showInfoWindow();
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setTrafficEnabled(true);

            Location location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (location != null) {

                loc = new LatLng(location.getLatitude(), location.getLongitude());
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                String LongAddress = "";
                try {
                    List<Address> Address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    String address = Address.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = Address.get(0).getLocality();
                    String state = Address.get(0).getAdminArea();
                    String country = Address.get(0).getCountryName();
                    String postalCode = Address.get(0).getPostalCode();

                    LongAddress = address + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f));
                MarkerOptions markerOptions = new MarkerOptions().position(loc).title(LongAddress).draggable(true);
                googleMap.addMarker(markerOptions);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
