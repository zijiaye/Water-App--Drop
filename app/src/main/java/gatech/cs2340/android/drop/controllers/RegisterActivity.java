package gatech.cs2340.android.drop.controllers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import gatech.cs2340.android.drop.R;
import gatech.cs2340.android.drop.model.User;


public class RegisterActivity extends AppCompatActivity {
    public static final List<String> legalUserType = Arrays.asList("User", "Worker", "Manager", "Admin");
    private static final String TAG = "RegisterActivity";
    private EditText _nameField;
    private EditText _emailField;
    private EditText _passwordField;
    private Spinner _userTypeSpinner;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set Spinner
        _userTypeSpinner = (Spinner) findViewById(R.id.user_type_spinner);

        //show in spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, legalUserType);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _userTypeSpinner.setAdapter(typeAdapter);

        //change spinner triangle color to white
        _userTypeSpinner.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        //Already a member button
        TextView login = (TextView) findViewById(R.id.register_log_in);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Login Button Clicked");
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        //Create Account button onClick
        Button create = (Button) findViewById(R.id.register_create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Create the account if valid
     */
    private void create() {
        Log.d(TAG, "Create Account Button Clicked");

        //Grab email and password input from login screen
        _nameField = (EditText) findViewById(R.id.register_name_input);
        _emailField = (EditText) findViewById(R.id.register_email_input);
        _passwordField = (EditText) findViewById(R.id.register_password_input);
        _userTypeSpinner = (Spinner) findViewById(R.id.user_type_spinner);

        if (!validate()) {
            onRegisterFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String name = _nameField.getText().toString().trim();
        final String email = _emailField.getText().toString().trim();
        final String password = _passwordField.getText().toString().trim();
        final String userType = (String)_userTypeSpinner.getSelectedItem();

        //FireBase auth code
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.email_error,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            //Add to FireBase database
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();
                            User user = new User(name, email, password, userType);
                            //get database instance
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(userId).setValue(user);
                            //display successful message
                            Toast.makeText(RegisterActivity.this, R.string.acct_successful,
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }

    /**
     * Show error when the information is invalid
     */
    private void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Incorrect name, email or password!", Toast.LENGTH_LONG).show();
    }

    /**
     * Validate if the registered account information is valid
     * @return if the account information is valid
     */
    private boolean validate() {
        Log.d(TAG, "Validate");
        boolean valid = true;

        String name = _nameField.getText().toString();
        String email = _emailField.getText().toString();
        String password = _passwordField.getText().toString();

        if (name.isEmpty()) {
            _nameField.setError("Enter a valid name");
            valid = false;
        } else {
            _nameField.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailField.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailField.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordField.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordField.setError(null);
        }

        return valid;
    }
}
