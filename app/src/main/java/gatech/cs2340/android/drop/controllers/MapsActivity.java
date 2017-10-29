package gatech.cs2340.android.drop.controllers;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import gatech.cs2340.android.drop.R;

import static java.lang.Double.parseDouble;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation_view);
        View view = bottomNavigationView.findViewById(R.id.ic_map);
        view.performClick();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_report:
                        startActivity(new Intent(MapsActivity.this, SourceReportActivity.class));
                        break;
                    case R.id.ic_map:
                        startActivity(new Intent(MapsActivity.this, MapsActivity.class));
                        break;
                    case R.id.ic_add:
                        startActivity(new Intent(MapsActivity.this, PurityReportActivity.class));
                        break;
                    case R.id.ic_graph:
                        startActivity(new Intent(MapsActivity.this, HistoricalActivity.class));
                        break;
                    case R.id.ic_setting:
                        startActivity(new Intent(MapsActivity.this, SettingActivity.class));
                        break;
                }
                return true;
            }

        });

        //button
        //welcome register button onClick
        Button map = (Button) findViewById(R.id.map_button);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Map Button Clicked");
                //Intent registerIntent = new Intent(MapsActivity.this, RegisterActivity.class);
                //startActivity(registerIntent);
                geoLocate();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("sourceReports");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String latitude = postSnapshot.child("_latitude").getValue(String.class);
                    String longitude = postSnapshot.child("_longitude").getValue(String.class);
                    Log.e("Get Data", latitude + longitude + "");
                    LatLng cityChoose = new LatLng(parseDouble(latitude), parseDouble(longitude));
                    mMap.addMarker(new MarkerOptions().position(cityChoose)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .snippet("Potential Water Source here"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder gc = new Geocoder(MapsActivity.this);
                List<Address> list;
                try {
                    list = gc.getFromLocation(latLng.latitude, latLng.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Address add = list.get(0);
                String name = add.getLocality();
                LatLng cityChoose = new LatLng(latLng.latitude, latLng.longitude);
                Log.d(TAG, name);
                Log.d(TAG, cityChoose.latitude + " " + cityChoose.longitude + "");
                //StringBuilder sw = new StringBuilder();
                //sw.append("In ").append(name);

                //Marker marker = mMap.addMarker(new MarkerOptions().position(cityChoose)
                        //.title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        //.snippet("Potential Water Source here"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(cityChoose));

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //StringBuilder sw = new StringBuilder();
//                sw.append(marker.getTitle());
//                sw.append(System.getProperty("line.separator"));
                //sw.append("Latitude: ").append(marker.getPosition().latitude);
                final double lat = marker.getPosition().latitude;
                //sw.append(System.getProperty("line.separator"));
                //sw.append("Longitude: ").append(marker.getPosition().longitude);
                final double lng = marker.getPosition().longitude;
                //sw.append(System.getProperty("line.separator"));
                // String msg = sw.toString();
                //Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_LONG).show();
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        final ViewGroup nullParent = null;
                        View view = getLayoutInflater().inflate(R.layout.info_window, nullParent);
//                        final TextView tvLocality = (TextView) view.findViewById(R.id.tv_locality);
//                        final TextView tvLat = (TextView) view.findViewById(R.id.tv_lat);
//                        final TextView tvLng = (TextView) view.findViewById(R.id.tv_lng);
//                        final TextView tvSnippet = (TextView) view.findViewById(R.id.tv_snippet);

                        database = FirebaseDatabase.getInstance();
                        mDatabase = database.getReference("sourceReports");
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    String latitude = postSnapshot.child("_latitude").getValue(String.class);
                                    String longitude = postSnapshot.child("_longitude").getValue(String.class);
                                    double lati_double = Double.parseDouble(latitude);
                                    double long_double = Double.parseDouble(longitude);

                                    if (lat == lati_double && lng == long_double) {
                                        Log.e("hi", "Info passed");
                                        //tvLocality.setText("Latitude: ");
                                        String lati_temp = "Latitude: " + lat;
                                        String lng_temp = "Longitude: " + lng;
                                        String waterType = postSnapshot.child("_waterType").getValue(String.class);
                                        String waterCondition = postSnapshot.child("_waterCondition").getValue(String.class);
                                        Toast.makeText(MapsActivity.this, lati_temp + "\n" + lng_temp + "\nType of water: " + waterType + "\nCondition of water: " + waterCondition, Toast.LENGTH_LONG).show();
                                        // tvLat.setText("Water Type: " + postSnapshot.child("_waterType").getValue(String.class));
                                        //tvLng.setText("Water Condition: " + postSnapshot.child("_waterCondition").getValue(String.class));
                                        break;
                                        //tvSnippet.setText(marker.getSnippet());
                                    }
//                                    Log.e("Get Data", latitude + longitude + "");
//                                    LatLng cityChoose = new LatLng();
//                                    mMap.addMarker(new MarkerOptions().position(cityChoose)
//                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                                            .snippet("Potential Water Source here"));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        //LatLng ll = marker.getPosition();
                        //tvLocality.setText(marker.getTitle());
                        //Log.e("ji", marker.getTitle()+"");
//                        tvLat.setText("Water Type: Stream");
//                        tvLng.setText("Water Condition: Potable");
//                        tvSnippet.setText(marker.getSnippet());

                        return view;
                    }
                });
                return false;
            }
        });
    }

    /**
     * Zoom into the location
     * @param lat latitude of the location
     * @param lng longitude of the location
     */
    private void goToLocationZoom(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) 15);
        mMap.moveCamera(update);
    }


    /**
     * get location of  the view
     */
    private void geoLocate() {
        //     * @param view view of the map
        EditText et = (EditText) findViewById(R.id.editText1);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(MapsActivity.this);
        List<Address> list;

        try {
            list = gc.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(MapsActivity.this, locality, Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lon = address.getLongitude();
        goToLocationZoom(lat, lon);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
