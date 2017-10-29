package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gatech.cs2340.android.drop.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText _emailField;
    private EditText _passwordField;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Login button onClick
        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Grab email and password input from login screen
                _emailField = (EditText) findViewById(R.id.login_email_input);
                _passwordField = (EditText) findViewById(R.id.login_password_input);
                if (validate()) {
                    String email = _emailField.getText().toString().trim();
                    String password = _passwordField.getText().toString().trim();

                    login(email, password);
                } else {
                    onLoginFailed();
                    //return;
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        //Retrieve Password link
        TextView retrievePassword = (TextView) findViewById(R.id.login_forgot_password);
        retrievePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passwordIntent = new Intent(LoginActivity.this, RetrievePasswordActivity.class);
                startActivity(passwordIntent);
            }
        });

        //Sign up link
        TextView signUp = (TextView) findViewById(R.id.login_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    /**
     * Login into the main dashboard
     */
    private void login(String email, String password) {
        Log.d(TAG, "Login Button Clicked");

/*        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();*/

        //FireBase auth code
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        //progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, SourceReportActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Show error when login failed
     */
    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Incorrect email or password!", Toast.LENGTH_LONG).show();
    }

    /**
     * Validate if the enter account information is valid
     * @return if the account info is valid
     */
    private boolean validate() {
        Log.d(TAG, "Validate");
        boolean valid = true;

        String email = _emailField.getText().toString();
        String password = _passwordField.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailField.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailField.setError(null);
        }

        if (password.isEmpty()) {
            _passwordField.setError("Password is empty");
            valid = false;
        } else {
            _passwordField.setError(null);
        }

        return valid;
    }
}
