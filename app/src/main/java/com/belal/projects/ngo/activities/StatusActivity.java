package com.belal.projects.ngo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StatusActivity extends AppCompatActivity {
    // toolbar
    private Toolbar mToolbar ;
    // firebase Auth
    private FirebaseAuth mAuth ;
    // Fields and button
    private TextInputEditText mStatus ;
    private Button mSaveChangesBtn ;
    // progress dialog
    private ProgressDialog mSaveChangesProgress ;
    // firebase Database
    private DatabaseReference mStatusDatabase ;
    private FirebaseUser mCurrentUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_status );

        // toolbar
        mToolbar = (Toolbar) findViewById( R.id.login_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Status" );

        // progress dialog
        mSaveChangesProgress = new ProgressDialog(this);

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // firebase Database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Current_User_Id = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( Current_User_Id );

        // fields
        mStatus = (TextInputEditText) findViewById( R.id.status_field );

        // to retrieve the value of txtview from mainactivity and store it in a string in status activity
        String status_value = getIntent().getStringExtra( "status_value" );
        mStatus.setText( status_value );

        // button
        mSaveChangesBtn = (Button) findViewById( R.id.status_save_changes_btn );
        mSaveChangesBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show progress dialog registring the user ...
                mSaveChangesProgress.setTitle("Save Changes");
                mSaveChangesProgress.setMessage( "Please wait while we Save Changes" );
                mSaveChangesProgress.setCanceledOnTouchOutside( false );
                mSaveChangesProgress.show();

                // get the value from the textiputlayout and set it into current user status update
                String status = mStatus.getText().toString();
                mStatusDatabase.child( "status" ).setValue( status ).addOnCompleteListener( new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           mSaveChangesProgress.dismiss();
                           // if status changed successfully show me toast and bring me back to main activity
                           Toast.makeText( StatusActivity.this, "Status Updated Successfuly", Toast.LENGTH_LONG );
                           Intent mainIntent = new Intent( StatusActivity.this, MainActivity.class);
                           mainIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                           startActivity( mainIntent );
                       }else{
                           Toast.makeText( StatusActivity.this, "Please Try Again", Toast.LENGTH_LONG );
                       }
                    }
                } );

            }
        } );

    }
}
