package com.belal.projects.ngo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegisterActivity extends AppCompatActivity {

    // toolbar
    private Toolbar mToolbar ;
    // firebase Auth
    private FirebaseAuth mAuth ;
    // Fields and button
    private TextInputEditText mFirstName ;
    private TextInputEditText mLastName ;
    private TextInputEditText mEmailAddress ;
    private TextInputEditText mPassword ;
    private TextInputEditText mPasswordAgain ;
    private Button mCreateButton ;
    // progress dialog
    private ProgressDialog mRegProgress ;
    // to check if the email is fake or not ...
    Boolean correctEmail = false ;
    // firebase database reference ...
    private DatabaseReference mDatabase ;
    // firebase user
    private FirebaseUser mCurrentUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        // toolbar
        mToolbar = (Toolbar) findViewById( R.id.reg_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Create New Account" );
        getSupportActionBar().setLogo(R.drawable.charitable_small_logo2);
        getSupportActionBar().setDisplayUseLogoEnabled( true );

        // progress dialog
        mRegProgress = new ProgressDialog(this);

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // fields
        mFirstName = (TextInputEditText) findViewById( R.id.reg_first_name_field );
        mLastName = (TextInputEditText) findViewById( R.id.reg_last_name_field );
        mEmailAddress = (TextInputEditText) findViewById( R.id.reg_email_field );
        mPassword = (TextInputEditText) findViewById( R.id.reg_password_field );
        mPasswordAgain = (TextInputEditText) findViewById( R.id.reg_password_again_field );

        // button
        mCreateButton = (Button) findViewById( R.id.reg_create_btn );
        mCreateButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String first_name = mFirstName.getText().toString();
                final String last_name = mLastName.getText().toString();
                final String email_address = mEmailAddress.getText().toString();
                final String password = mPassword.getText().toString();
                final String password_again = mPasswordAgain.getText().toString();

                // regex ... regular expression to detect valid email from unvalid email ...
                if( isValidEmailId(email_address) ){
                    correctEmail = true ;
                }else{
                    correctEmail = false ;
                    Toast.makeText(getApplicationContext(), "Wrong Email Address !", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty( first_name ) || TextUtils.isEmpty( last_name ) ||
                        TextUtils.isEmpty( email_address ) || TextUtils.isEmpty( password ) || TextUtils.isEmpty( password_again ) ) {

                    // please dont crash ... i hate you ... ( update >> it works fuck i love you )
                    mRegProgress.hide();
                    validate_reg(first_name, last_name, email_address, password, password_again);
                }else{
                    // show progress dialog registring the user ...
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage( "Please wait while we create your account" );
                    mRegProgress.setCanceledOnTouchOutside( false );
                    mRegProgress.show();

                    if (correctEmail == true){
                        register_user(first_name, last_name, email_address, password, password_again);
                    }else{
                        mRegProgress.hide();
                        Toast.makeText( RegisterActivity.this, "Wrong Email Address !", Toast.LENGTH_LONG ).show();
                    }
                }
            }
        } );
    } // onCreate


    private void validate_reg(String first_name, String last_name, String email_address, String password, String password_again) {
        // validating the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder( "Please, insert " );

        if (isEmpty(first_name)){
            validationError = true ;
            validationErrorMessage.append( " First Name , " );
        }

        if (isEmpty(last_name)){
            validationError = true ;
            validationErrorMessage.append( " Last Name , " );
        }

        if (isEmpty(email_address)){
            validationError = true ;
            validationErrorMessage.append( " Email Address , " );
        }

        if (isEmpty( password )){
            if (validationError){
                validationErrorMessage.append( " and " );
            }
            validationError = true ;
            validationErrorMessage.append( " Password " );
        }
        if (isEmpty( password_again )) {
            if (validationError) {
                validationErrorMessage.append( " and " );
            }
            validationError = true;
            validationErrorMessage.append( "your Password Again" );
        }else{
            if (!isMatching(password, password_again )){
                if (validationError) {
                    validationErrorMessage.append( " and " );
                }
                validationError = true ;
                validationErrorMessage.append( "the same Password Twice. " );
            }
        }

        validationErrorMessage.append( "." );

        if (validationError) {
            mRegProgress.hide();
            Toast.makeText( RegisterActivity.this, validationErrorMessage.toString() , Toast.LENGTH_LONG ).show();
            return;
        }
    }

    private boolean isMatching(String password, String password_again) {
        if (password.equals( password_again )){
            return true;
        }else {
            return false;
        }
    }

    private boolean isEmpty(String txt) {
        if (txt.length() > 0 ){
            return false;
        }else {
            return true;
        }
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void register_user(final String first_name, final String last_name,
                               final String email_address, final String password, final String password_again) {

        mAuth.createUserWithEmailAndPassword(email_address, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // this is how we can get the current user id
                            mCurrentUser = mAuth.getCurrentUser();
                            String user_id = mCurrentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child( "User" ).child( user_id );

                            HashMap<String, String> userMap = new HashMap <>(  );
                            userMap.put( "first_name", first_name );
                            userMap.put( "last_name", last_name );
                            userMap.put( "status", "Hi there , i'm using CharitAble App" );
                            userMap.put( "profile_image", "default" );
                            userMap.put( "thumb_image", "default" );
                            mDatabase.setValue( userMap ).addOnCompleteListener( new OnCompleteListener <Void>() {
                                @Override
                                public void onComplete(@NonNull Task <Void> task) {
                                    // Sign in success, update UI with the signed-in user's information
                                    // if the register is successful before moving to another intent .. dismiss the progress dialog
                                    mRegProgress.dismiss();

                                    Intent mainIntent = new Intent( RegisterActivity.this, MainActivity.class);
                                    mainIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                    startActivity( mainIntent );
                                    finish();

                                    Toast.makeText( RegisterActivity.this, "Done", Toast.LENGTH_LONG ).show();
                                }
                            } );

                        } else {
                            // If sign in fails, display a message to the user.
                            // and if we got some errors we will hide it instead of dismissing it ...
                            mRegProgress.hide();
                            validate_reg(first_name, last_name, email_address, password, password_again);
                        }

                        // ...
                    }
                });
    }
}
