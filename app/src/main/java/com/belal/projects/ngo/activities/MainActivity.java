package com.belal.projects.ngo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.fragments.ChatFragment;
import com.belal.projects.ngo.fragments.HomeFragment;
import com.belal.projects.ngo.fragments.MoreFragment;
import com.belal.projects.ngo.fragments.MyAccountFragment;
import com.belal.projects.ngo.fragments.NGOsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    // firebase Auth
    private FirebaseAuth mAuth ;
    // firebase Database
    private DatabaseReference mDatabase ;
    private FirebaseUser mCurrentUser ;

    // firebase storage ...
    private StorageReference mImageStorage;
    // progress dialog
    private ProgressDialog mProgressDialog ;

    // toolbar
    private Toolbar mToolbar ;
    // bottom navigation bar
    private BottomNavigationView mBottomNavigationView ;

    private String download_url ;
    private String thumb_download_url ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mImageStorage = FirebaseStorage.getInstance().getReference();

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // toolbar
        mToolbar = (Toolbar) findViewById( R.id.main_app_bar );

        setSupportActionBar( mToolbar );
        //getSupportActionBar().setTitle( "Home" );
        getSupportActionBar().setLogo(R.drawable.charitable_small_logo2);
        getSupportActionBar().setDisplayUseLogoEnabled( true );

        // bottom navigation bar
        mBottomNavigationView = findViewById( R.id.main_bottom_nav_bar );
        mBottomNavigationView.setOnNavigationItemSelectedListener( navListener );
        getSupportFragmentManager().beginTransaction().replace( R.id.main_fragment_container, new HomeFragment() ).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if the user is signed in (not-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendToStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isAnonymous()){
            currentUser.delete();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent( MainActivity.this, StartActivity.class );
        startActivity( startIntent );
        finish();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null ;
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.nav_ngo:
                    selectedFragment = new NGOsFragment();
                    break;

                case R.id.nav_chat:
                    selectedFragment = new ChatFragment();
                    break;

                case R.id.nav_my_account:
                    selectedFragment = new MyAccountFragment();
                    break;

                case R.id.nav_more:
                    selectedFragment = new MoreFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace( R.id.main_fragment_container, selectedFragment ).commit();
            return true ;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog( MainActivity.this );
                mProgressDialog.setTitle( "Uploading Image ... " );
                mProgressDialog.setMessage( "Please wait while upload and process the image" );
                mProgressDialog.setCanceledOnTouchOutside( false );
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                // we gonna make this to get the image path to compress it ...
                File thumb_filePath = new File( resultUri.getPath() );

                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String Current_User_Id = mCurrentUser.getUid();

                Bitmap thumb_bitmap = null;  // <<<< to avoid the error and also make try and catch ...
                try {
                    thumb_bitmap = new Compressor( MainActivity.this )
                            .setMaxWidth( 200 )
                            .setMaxHeight( 200 )
                            .setQuality( 75 )
                            .compressToBitmap( thumb_filePath );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // this converts bitmap data into bytes so we could upload it to storage
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress( Bitmap.CompressFormat.JPEG, 100, baos );
                final byte[] thumb_bytes = baos.toByteArray();



                mDatabase = FirebaseDatabase.getInstance().getReference().child( "User" ).child( Current_User_Id );

                final StorageReference profile_filepath = mImageStorage.child( "profile_images" ).child( Current_User_Id + ".jpg" );
                // create another storage reference for our thumbnail images ...
                final StorageReference thumb_filepath = mImageStorage.child( "profile_images" ).child( "thumbs" ).child( Current_User_Id + ".jpg" );


                profile_filepath.putFile( resultUri ).addOnCompleteListener( new OnCompleteListener <UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task <UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            // to get the image URL and store it to current user data
                            profile_filepath.getDownloadUrl().addOnSuccessListener( new OnSuccessListener <Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    download_url = uri.toString();
                                }
                            } );


                            UploadTask uploadTask = thumb_filepath.putBytes( thumb_bytes );
                            uploadTask.addOnCompleteListener( new OnCompleteListener <UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task <UploadTask.TaskSnapshot> thumb_task) {
                                    // to get the thumbnail image URL and store it to current user data
                                    thumb_download_url = thumb_task.getResult().getStorage().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {
                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put( "profile_image", download_url );
                                        update_hashMap.put( "thumb_image", thumb_download_url );
                                        mDatabase.updateChildren( update_hashMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText( MainActivity.this, "Success Uploading", Toast.LENGTH_LONG ).show();
                                                }
                                            }
                                        } );

                                    } else {
                                        Toast.makeText( MainActivity.this, "Error in uploading Thumbnail ", Toast.LENGTH_LONG ).show();
                                        mProgressDialog.dismiss();
                                    }
                                }
                            } );

                        } else {
                            Toast.makeText( MainActivity.this, "Error in uploading ! ", Toast.LENGTH_LONG ).show();
                            mProgressDialog.dismiss();
                        }
                    }
                } );

                //********************************************************************************************************
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }//onActivity Result


}


