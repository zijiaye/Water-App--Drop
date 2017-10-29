package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import gatech.cs2340.android.drop.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        //welcome register button onClick
        Button register = (Button) findViewById(R.id.welcome_register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Register Button Clicked");
                Intent registerIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        //welcome login button clicked
        Button login = (Button) findViewById(R.id.welcome_login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Welcome Sign In Button Clicked");
                Intent loginInIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(loginInIntent);
            }
        });
    }
}
