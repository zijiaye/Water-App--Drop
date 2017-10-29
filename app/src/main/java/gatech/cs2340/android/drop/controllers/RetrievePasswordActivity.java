package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import gatech.cs2340.android.drop.R;

public class RetrievePasswordActivity extends AppCompatActivity {

    //private FireBaseAuth auth;
    //private EditText _emailField;
    private static final String TAG = "RetrievePassActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        //Create Account button onClick
        Button retrieve = (Button) findViewById(R.id.retrieve_button);
        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassword();
            }
        });

    }

    private void getPassword() {
        //get email info
        EditText _emailField = (EditText) findViewById(R.id.retrieve_email_input);
        String emailAddress = _emailField.getText().toString().trim();

        //send password reset
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(RetrievePasswordActivity.this, "Email Sent",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RetrievePasswordActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RetrievePasswordActivity.this, "Email does not exist",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
