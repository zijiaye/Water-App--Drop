package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import gatech.cs2340.android.drop.R;
import gatech.cs2340.android.drop.model.PurityReport;
import gatech.cs2340.android.drop.model.User;

public class AddPurityReportActivity extends AppCompatActivity {

    private static final String TAG = "AddSourceReportActivity";
    final private List<String> legalOverallCondition = Arrays.asList("Safe", "Treatable", "Unsafe");
    private EditText _lati;
    private EditText _long;
    private Spinner _conditionTypeSpinner;
    private EditText _virus;
    private EditText _contaminant;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purity_report);

        //Set Spinner
        _conditionTypeSpinner = (Spinner) findViewById(R.id.overall_condition_spinner);

        //show in spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, legalOverallCondition);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _conditionTypeSpinner.setAdapter(typeAdapter);

        //change spinner triangle color to white
        _conditionTypeSpinner.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        Button createSp = (Button) findViewById(R.id.create_pr);
        createSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createReport();
            }
        });

        Button cancelSp = (Button) findViewById(R.id.cancel_pr);
        cancelSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Cancel Button Clicked");
                Toast.makeText(AddPurityReportActivity.this, "Report Cancelled",
                        Toast.LENGTH_LONG).show();
                Intent cancelIntent = new Intent(AddPurityReportActivity.this, PurityReportActivity.class);
                startActivity(cancelIntent);
            }
        });
    }

    private void createReport() {
        _lati = (EditText) findViewById(R.id.pr_lat);
        _long = (EditText) findViewById(R.id.pr_long);
        _virus = (EditText) findViewById(R.id.pr_virus);
        _contaminant = (EditText) findViewById(R.id.pr_contaminant);


        final String latitude = _lati.getText().toString().trim();
        final String longitude = _long.getText().toString().trim();
        final String virus = _virus.getText().toString().trim();
        final String contaminant = _contaminant.getText().toString().trim();

        final String overallCondition = (String)_conditionTypeSpinner.getSelectedItem();

        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT,
                Locale.US);
        Date date = new Date();
        final String timeStamp = dateFormat.format(date);
        String id = timeStamp.replace("/", "");
        final String reportId = id.replace(":", "");

        Random rand = new Random(System.currentTimeMillis());
        final String reportNum = Math.abs(rand.nextInt()/1000000) + "";

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String uid = user.getUid();
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("users");

        if (!validate()) {
            onRegisterFailed();
            return;
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User userInfo = dataSnapshot.child(uid).getValue(User.class);
                PurityReport pr = new PurityReport(timeStamp, reportNum, userInfo._name,
                        latitude, longitude, overallCondition,virus, contaminant);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("purityReports").child(reportId).setValue(pr);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        Log.d(TAG, "Create Source Report Clicked");
        Toast.makeText(AddPurityReportActivity.this, "Report Created!",
                Toast.LENGTH_LONG).show();
        Intent sourceIntent = new Intent(AddPurityReportActivity.this, PurityReportActivity.class);
        startActivity(sourceIntent);
    }

    private void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Invalid Information!", Toast.LENGTH_LONG).show();
    }

    private boolean validate() {
        Log.d(TAG, "Validate purity report");
        boolean valid = true;

        String latitude = _lati.getText().toString().trim();
        String longitude = _long.getText().toString().trim();
        String virus = _virus.getText().toString().trim();
        String contaminant = _contaminant.getText().toString().trim();

        if (latitude.isEmpty() || latitude.length() > 6) {
            _lati.setError("Enter a valid latitude");
            valid = false;
        } else {
            _lati.setError(null);
        }

        if (longitude.isEmpty() || longitude.length() > 6) {
            _long.setError("Enter a valid longitude");
            valid = false;
        } else {
            _long.setError(null);
        }

        if (virus.isEmpty() || virus.length() > 6) {
            _virus.setError("Enter a valid virus PPM");
            valid = false;
        } else {
            _virus.setError(null);
        }

        if (contaminant.isEmpty() || contaminant.length() > 6) {
            _contaminant.setError("Enter a valid contaminant PPM");
            valid = false;
        } else {
            _contaminant.setError(null);
        }

        return valid;
    }
}
