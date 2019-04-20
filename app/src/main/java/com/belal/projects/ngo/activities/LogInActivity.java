package com.belal.projects.ngo.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LogInActivity extends AppCompatActivity {
    // toolbar
    private Toolbar mToolbar ;
    // firebase Auth
    private FirebaseAuth mAuth ;
    // Fields and button
    private TextInputEditText mEmailAddress ;
    private TextInputEditText mPassword ;
    private Button mLoginButton ;
    // progress dialog
    private ProgressDialog mLoginProgress ;
    // forgot password txt
    private TextView mForgotPassword ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log_in );

        // toolbar
        mToolbar = (Toolbar) findViewById( R.id.login_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Log In" );
        getSupportActionBar().setLogo(R.drawable.charitable_small_logo2);
        getSupportActionBar().setDisplayUseLogoEnabled( true );

        // progress dialog
        mLoginProgress = new ProgressDialog(this);

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // fields
        mEmailAddress = (TextInputEditText) findViewById( R.id.login_email_field );
        mPassword = (TextInputEditText) findViewById( R.id.login_password_field );

        // button
        mLoginButton = (Button) findViewById( R.id.login_btn );
        mLoginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_address = mEmailAddress.getText().toString();
                String password = mPassword.getText().toString();

                if( TextUtils.isEmpty( email_address ) || TextUtils.isEmpty( password ) ) {
                    // please dont crash ... i hate you ... ( update >> it works fuck i love you )
                    mLoginProgress.hide();
                    validate_login(email_address, password);

                }else{

                    // show progress dialog registring the user ...
                    mLoginProgress.setTitle("Loging In User");
                    mLoginProgress.setMessage( "Please wait while we Log In into your account" );
                    mLoginProgress.setCanceledOnTouchOutside( false );
                    mLoginProgress.show();
                    login_user( email_address, password );
                }
            }
        } );

        // forgot password
        mForgotPassword = (TextView) findViewById( R.id.login_forgot_password_txt );
        mForgotPassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_address = mEmailAddress.getText().toString();
                if( TextUtils.isEmpty(email_address) ){
                    Toast.makeText( LogInActivity.this, "Write Your Email Address First !", Toast.LENGTH_LONG ).show();
                }else{
                    mAuth.sendPasswordResetEmail( email_address ).addOnCompleteListener( new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task <Void> task) {
                            AlertDialog alertDialog = new AlertDialog.Builder(LogInActivity.this).create();
                            alertDialog.setTitle("Forgot Password");
                            alertDialog.setMessage("Check Your Email Address to Reset Your Password !");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    } );
                }
            }
        } );
    }

    private void login_user(final String email_address, final String password) {
        mAuth.signInWithEmailAndPassword( email_address, password ).addOnCompleteListener( new OnCompleteListener <AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // if the login is successful before moving to another intent ... dismiss the progress dialog
                    mLoginProgress.dismiss();
                    Intent mainIntent = new Intent( LogInActivity.this, MainActivity.class);
                    mainIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity( mainIntent );
                    finish();
                    Toast.makeText( LogInActivity.this, "Done", Toast.LENGTH_LONG ).show();

                }else {
                    // and if we got some errors ... we will hide it instead of dismissing it ....
                    mLoginProgress.hide();
                    validate_login(email_address, password);

                }
            }
        } );
    }

    private void validate_login(String email_address, String password) {
        // validating the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder( "Please, insert " );

        if (isEmpty(email_address)){
            validationError = true ;
            validationErrorMessage.append( " Email Address" );
        }

        if (isEmpty( password )){
            if (validationError){
                validationErrorMessage.append( " and " );
            }
            validationError = true ;
            validationErrorMessage.append( " Password " );
        }

        validationErrorMessage.append( "." );

        if (validationError) {
            mLoginProgress.hide();
            Toast.makeText( LogInActivity.this, validationErrorMessage.toString() , Toast.LENGTH_LONG ).show();
            return;
        }
    }

    private boolean isEmpty(String txt) {
        if (txt.length() > 0 ){
            return false;
        }else {
            return true;
        }
    }

}
