package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.List;

import gatech.cs2340.android.drop.R;
import gatech.cs2340.android.drop.model.GraphData;
import gatech.cs2340.android.drop.model.PurityReport;
import gatech.cs2340.android.drop.model.User;

public class HistoricalActivity extends AppCompatActivity {

    private static final String TAG = "HistoricalActivity";
    private final List<String> legalType = Arrays.asList("Virus", "Contaminant");
    private EditText _lat;
    private EditText _long;
    private EditText _year;
    private Spinner _typeSpinner;
    private DatabaseReference mDatabase;
    //private FireBaseUser user;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);

        //hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation_view);
        View view = bottomNavigationView.findViewById(R.id.ic_graph);
        view.performClick();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_report:
                        startActivity(new Intent(HistoricalActivity.this, SourceReportActivity.class));
                        break;
                    case R.id.ic_map:
                        startActivity(new Intent(HistoricalActivity.this, MapsActivity.class));
                        break;
                    case R.id.ic_add:
                        startActivity(new Intent(HistoricalActivity.this, PurityReportActivity.class));
                        break;
                    case R.id.ic_graph:
                        startActivity(new Intent(HistoricalActivity.this, HistoricalActivity.class));
                        break;
                    case R.id.ic_setting:
                        startActivity(new Intent(HistoricalActivity.this, SettingActivity.class));
                        break;
                }
                return true;
            }

        });

        // set spinner
        _typeSpinner = (Spinner) findViewById(R.id.vSpinner);

        // show in spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, legalType);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _typeSpinner.setAdapter(typeAdapter);

        // change spinner triangle color to white
        //PorterDuff.Mode.SRC.SRC_ATOP
        _typeSpinner.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        final Button submitSp = (Button) findViewById(R.id.submit_his);

        //get profile from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String uid = user.getUid();
        // Read from the database
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User userInfo = dataSnapshot.child(uid).getValue(User.class);
                if (userInfo._userType.equalsIgnoreCase("User") || userInfo._userType.equalsIgnoreCase("Worker")) {
                    submitSp.setEnabled(false);
                    submitSp.setTextColor(Color.parseColor("#808080"));
                    //submitSp.setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        _lat = (EditText) findViewById(R.id.historical_lat);
        _long = (EditText) findViewById(R.id.historical_long);
        _year = (EditText) findViewById(R.id.historical_year);

        submitSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistory();
            }
        });
    }

    private void showHistory() {
        final String lat = _lat.getText().toString().trim();
        final String lon = _long.getText().toString().trim();
        final String year = _year.getText().toString().trim();
        final String PPMType = (String) _typeSpinner.getSelectedItem();

        if(!validate()) {
            return;
        }

        Log.e(TAG, lat + lon + year + PPMType);
        mDatabase = database.getReference("purityReports");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                boolean isData = false;
                GraphData[] graphData = new GraphData[13];
                int totalCount = (int) dataSnapshot.getChildrenCount();
                Log.e(TAG, "111: " + totalCount + "");
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PurityReport rp = snapshot.getValue(PurityReport.class);
                    count++;
                    for(int i = 0; i < graphData.length; i++) {
                        graphData[i] = new GraphData();
                    }
                    if(lat.equals(rp._latitude) && lon.equals(rp._latitude) && year.equals(rp._date.substring(0, 4))) {
                        isData = true;
                        String month = rp._date.substring(6,7);
                        if (month.charAt(0) == '0') {
                            month = month.charAt(1) + "";
                        }
                        int index = Integer.parseInt(month);
                        if (PPMType.equals("Virus")) {
                            graphData[index].addGraphData(rp._virusPPM);
                            Log.e(TAG, index + " " + rp._virusPPM + "");
                        } else if (PPMType.equals("Contaminant")) {
                            graphData[index].addGraphData(rp._contaminant);
                        }
                    }
                    Log.e(TAG, "111: " + count + "");
                    if (isData) {
                        GraphView graph = (GraphView) findViewById(R.id.graph);
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                                new DataPoint(1, graphData[1].getAvgPPM()),
                                new DataPoint(2, graphData[2].getAvgPPM()),
                                new DataPoint(3, graphData[3].getAvgPPM()),
                                new DataPoint(4, graphData[4].getAvgPPM()),
                                new DataPoint(5, graphData[5].getAvgPPM()),
                                new DataPoint(6, graphData[6].getAvgPPM()),
                                new DataPoint(7, graphData[7].getAvgPPM()),
                                new DataPoint(8, graphData[8].getAvgPPM()),
                                new DataPoint(9, graphData[9].getAvgPPM()),
                                new DataPoint(10, graphData[10].getAvgPPM()),
                                new DataPoint(11, graphData[11].getAvgPPM()),
                                new DataPoint(12, graphData[12].getAvgPPM())
                        });
                        Log.e(TAG, "333: " + graphData[3].getAvgPPM() + "");
                        graph.addSeries(series);
                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMinX(1);
                        graph.getViewport().setMaxX(13);
                        graph.getGridLabelRenderer().setVerticalAxisTitle("PPM");
                        graph.getGridLabelRenderer().setHorizontalAxisTitle("Month");
                    } else {
                        Toast.makeText(HistoricalActivity.this, "No Data Found!",
                                Toast.LENGTH_LONG).show();
                    }

                }
                Log.e(TAG, graphData[3].getAvgPPM() + "");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private boolean validate() {
        Log.d(TAG, "Validate");
        boolean valid = true;

        String lat = _lat.getText().toString().trim();
        String lon = _long.getText().toString().trim();
        String year = _year.getText().toString().trim();


        if (lat.isEmpty()) {
            _lat.setError("Enter a valid latitude");
            valid = false;
        } else {
            _lat.setError(null);
        }

        if (lon.isEmpty()) {
            _long.setError("Enter a valid longitude");
            valid = false;
        } else {
            _long.setError(null);
        }

        if (year.isEmpty()) {
            _year.setError("Enter a valid year");
            valid = false;
        } else {
            _year.setError(null);
        }

        return valid;
    }
}