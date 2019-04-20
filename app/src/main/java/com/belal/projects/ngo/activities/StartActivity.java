package com.belal.projects.ngo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    // firebase Auth
    private FirebaseAuth mAuth ;
    // the texts and buttons
    private TextView mSkip ;
    private TextView mNewAccount ;
    private Button mWithEmailAddress ;
    private Button mGmail ;
    // progress dialog
    private ProgressDialog mRegProgress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_start );

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // progress dialog
        mRegProgress = new ProgressDialog(this);

        mNewAccount = (TextView) findViewById( R.id.start_newAccount_txt );
        mNewAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntert = new Intent( StartActivity.this, RegisterActivity.class );
                startActivity( regIntert );
            }
        } );

        mSkip = (TextView) findViewById( R.id.start_skip_txt );
        mSkip.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show progress dialog registring the user ...
                mRegProgress.setTitle("Continue as Guest");
                mRegProgress.setMessage( "Please wait For Seconds ..." );
                mRegProgress.setCanceledOnTouchOutside( false );
                mRegProgress.show();
                visitor();
            }
        } );

        mWithEmailAddress = (Button) findViewById( R.id.start_withEmail_btn );
        mWithEmailAddress.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntert = new Intent( StartActivity.this, LogInActivity.class );
                startActivity( loginIntert );
            }
        } );

        mGmail = (Button) findViewById( R.id.start_gmail_btn );
        mGmail.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );


    } // onCreate

    private void visitor() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mRegProgress.dismiss();

                            Intent skipIntent = new Intent( StartActivity.this, MainActivity.class);
                            startActivity( skipIntent );

                        } else {
                            // If sign in fails, display a message to the user.
                            mRegProgress.hide();
                            Toast.makeText( StartActivity.this, "Please Try Again" , Toast.LENGTH_LONG ).show();
                        }

                        // ...
                    }
                });
    }

}
