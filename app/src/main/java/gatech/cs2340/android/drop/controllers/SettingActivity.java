package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import gatech.cs2340.android.drop.R;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation_view);
        View view = bottomNavigationView.findViewById(R.id.ic_setting);
        view.performClick();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_report:
                        startActivity(new Intent(SettingActivity.this, SourceReportActivity.class));
                        break;
                    case R.id.ic_map:
                        startActivity(new Intent(SettingActivity.this, MapsActivity.class));
                        break;
                    case R.id.ic_add:
                        startActivity(new Intent(SettingActivity.this, PurityReportActivity.class));
                        break;
                    case R.id.ic_graph:
                        startActivity(new Intent(SettingActivity.this, HistoricalActivity.class));
                        break;
                    case R.id.ic_setting:
                        startActivity(new Intent(SettingActivity.this, SettingActivity.class));
                        break;
                }
                return true;
            }

        });

        //Edit profile button clicked
        ImageButton editProfile = (ImageButton) findViewById(R.id.edit_profile_icon);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Edit profile Button Clicked");
                Intent editProfileInIntent = new Intent(SettingActivity.this, EditProfileActivity.class);
                startActivity(editProfileInIntent);
            }
        });

        //Security Log button clicked
        ImageButton securityLog = (ImageButton) findViewById(R.id.security_log);
        securityLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Security Log Button Clicked");
                Toast.makeText(SettingActivity.this, "Security Log not yet implemented!",
                        Toast.LENGTH_LONG).show();
            }
        });

        //Log out button clicked
        ImageButton logout = (ImageButton) findViewById(R.id.setting_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Log Out Button Clicked");
                FirebaseAuth.getInstance().signOut();
                Intent editProfileInIntent = new Intent(SettingActivity.this, WelcomeActivity.class);
                startActivity(editProfileInIntent);
            }
        });
    }
}
