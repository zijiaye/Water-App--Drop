package gatech.cs2340.android.drop.controllers;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gatech.cs2340.android.drop.R;
import gatech.cs2340.android.drop.model.User;

import static gatech.cs2340.android.drop.controllers.RegisterActivity.legalUserType;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private Spinner _userTypeSpinner;
    private EditText _nameText;
    private EditText _emailText;
    private EditText _passwordText;
    //private FireBaseUser user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Set Spinner
        _userTypeSpinner = (Spinner) findViewById(R.id.edit_profile_type_spinner);

        //show in spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, legalUserType);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _userTypeSpinner.setAdapter(typeAdapter);

        //change spinner triangle color to white
        _userTypeSpinner.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        //get email and password
        _passwordText = (EditText) findViewById(R.id.edit_profile_password_input);
        _emailText = (EditText) findViewById(R.id.edit_profile_email_input);
        _nameText = (EditText) findViewById(R.id.edit_profile_name_input);

        //get profile from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String uid = user.getUid();
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User userInfo = dataSnapshot.child(uid).getValue(User.class);
                _nameText.setText(userInfo._name, TextView.BufferType.EDITABLE);
                _emailText.setText(userInfo._email, TextView.BufferType.EDITABLE);
                _passwordText.setText(userInfo._password, TextView.BufferType.EDITABLE);
                _userTypeSpinner.setSelection(getIndex(_userTypeSpinner, userInfo._userType));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        Button saveProfile = (Button) findViewById(R.id.edit_profile_save_button);
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    updateAccount();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Invalid email or password!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Get index of saved string of the spinner
     * @param spinner name of the spinner
     * @param myString string to search
     * @return the index of the string
     */
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    /**
     * Validate if the account information is valid
     * @return if account is valid
     */
    private boolean validate() {
        Log.d(TAG, "Validate");
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String name = _nameText.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError("Enter a valid name");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Password is empty");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /**
     * update the account information in FireBase
     */
    private void updateAccount() {

        String name = _nameText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        String userType = (String) _userTypeSpinner.getSelectedItem();

        //update FireBase auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });


        //update FireBase database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        User userInfo = new User(name, email, password, userType);
        mDatabase.child("users").child(userId).setValue(userInfo);

        //display message
        Toast.makeText(EditProfileActivity.this, "Profile Updated!",
                Toast.LENGTH_LONG).show();

        Intent settingIntent = new Intent(EditProfileActivity.this, SettingActivity.class);
        startActivity(settingIntent);
    }
}
