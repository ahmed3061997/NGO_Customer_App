package com.belal.projects.ngo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.adapters.MessageAdapter;
import com.belal.projects.ngo.models.Message;
import com.belal.projects.ngo.models.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String mNgoID;
    private String mMyUserName;

    private androidx.appcompat.widget.Toolbar mChatToolbar;
    private DatabaseReference mRootRef;

    private TextView mTitleView;
    private CircleImageView mNgoImage;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final ArrayList <Message> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrentPage = 1;

    // New Solution ...
    private int itemPos = 0;

    private String mLastKey;
    private String mPrevKey;

    private DatabaseReference mUserDatabase ;
    private DatabaseReference mNgoDatabase ;

    String mMyProfileImage;

    // firebase storage ...
    private StorageReference mImageStorage;

    private String download_url ;

    // progress dialog
    private ProgressDialog mProgressDialog ;



    // We need to pass in the integar for the request
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        // Tool bar
        mChatToolbar = (androidx.appcompat.widget.Toolbar) findViewById( R.id.chat_app_bar );
        setSupportActionBar( mChatToolbar );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled( true );
        actionBar.setDisplayShowCustomEnabled( true );

        // get the current user id
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // firebase storage ...
        mImageStorage = FirebaseStorage.getInstance().getReference();

        // firebase database
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mCurrentUserId );
        mUserDatabase.keepSynced( true ); // <<<<< for offline capabilities


        // get the current user info
        mUserDatabase.addValueEventListener( new ValueEventListener() {
            @Override // onDataChange when retrive data add or change ...
            public void onDataChange(DataSnapshot dataSnapshot) {
                String first_name = dataSnapshot.child( "first_name" ).getValue().toString();
                String last_name = dataSnapshot.child( "last_name" ).getValue().toString();
                String status = dataSnapshot.child( "status" ).getValue().toString();
                final String profile_image = dataSnapshot.child( "profile_image" ).getValue().toString();
                final String thumb_image = dataSnapshot.child( "thumb_image" ).getValue().toString();

                Bundle bundle = new Bundle();
                bundle.getString( "profile_image", profile_image );

                mMyUserName = first_name + " " + last_name;
                mMyProfileImage = profile_image;

            }

            @Override // onCancelled to handle the errors ...
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        // get the ngo info from intent extra
        mNgoID = getIntent().getStringExtra( "ngo_id" );


        mNgoDatabase = FirebaseDatabase.getInstance().getReference().child( "Ngos" ).child( mNgoID );
        // get the ngo info
        mNgoDatabase.addValueEventListener( new ValueEventListener() {
            @Override // onDataChange when retrive data add or change ...
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ngoName = dataSnapshot.child( "org_name" ).getValue().toString();
                String ngoLogo = dataSnapshot.child( "org_logo" ).getValue().toString();

                Toast.makeText( ChatActivity.this, ngoName, Toast.LENGTH_SHORT ).show();

                mTitleView.setText( ngoName );
                Picasso.get().load( ngoLogo ).placeholder( R.drawable.default_ngo_logo ).into( mNgoImage );

            }


            @Override // onCancelled to handle the errors ...
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        // custom chat bar
        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View action_bar_view = inflater.inflate( R.layout.chat_custom_bar, null );
        actionBar.setCustomView( action_bar_view );

        // ------------ Custom Action bar Items --------------------------------------------
        mTitleView = (TextView) findViewById( R.id.custom_bar_title );
        mNgoImage = (CircleImageView) findViewById( R.id.custom_bar_image );
        mChatAddBtn = (ImageButton) findViewById( R.id.chat_add_btn );
        mChatSendBtn = (ImageButton) findViewById( R.id.chat_send_btn );
        mChatMessageView = (EditText) findViewById( R.id.chat_message_view );

        //*****************************************************************

        // recycler view and adapter
        mAdapter = new MessageAdapter( this, messagesList );
        mMessagesList = (RecyclerView) findViewById( R.id.messages_list );
        mRefreshLayout = (SwipeRefreshLayout) findViewById( R.id.message_swipe_layout );

        mLinearLayout = new LinearLayoutManager( this );
        mMessagesList.setHasFixedSize( true );
        mMessagesList.setLayoutManager( mLinearLayout );
        mMessagesList.setAdapter( mAdapter );

        loadMessages();

        //*****************************************************************

        // add chat to database
        mRootRef.child( "Chats" ).child( mCurrentUserId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild( mNgoID )) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put( "seen", false );
                    chatAddMap.put( "timestamp", ServerValue.TIMESTAMP );

                    Map chatUserMap = new HashMap();
                    chatUserMap.put( "Chats/" + mCurrentUserId + "/" + mNgoID, chatAddMap );
                    chatUserMap.put( "Chats/" + mNgoID + "/" + mCurrentUserId, chatAddMap );

                    mRootRef.updateChildren( chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d( "CHAT_LOG", databaseError.getMessage().toString() );
                            }
                        }
                    } );

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        // send button
        mChatSendBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        } );

        // add image button
        mChatAddBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines( CropImageView.Guidelines.ON )
                        .setCropMenuCropButtonTitle( "Send" )
                        .setActivityTitle( "SEND IMAGE" )
                        .setActivityMenuIconColor( R.color.colorPrimary )
                        .start( ChatActivity.this );  // this >>>> SettingsActivity.this
            }
        } );


        // refresh layout
        mRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();

            }
        } );

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            mUserRef = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mCurrentUser.getUid() );
        }


    } // onCreate



    private void loadMoreMessages() {
        DatabaseReference messageRef = mRootRef.child( "Messages" ).child( mCurrentUserId ).child( mNgoID );

        Query messageQuery = messageRef.orderByKey()
                .endAt( mLastKey ).limitToLast( 20 );

        messageQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue( Message.class );
                String messageKey = dataSnapshot.getKey();

//                messagesList.add( itemPos++, message );

                if (!mPrevKey.equals( messageKey )) {
                    messagesList.add( itemPos++, message );
                } else {
                    mPrevKey = mLastKey;
                }

                if (itemPos == 1) {
                    mLastKey = messageKey;
                }


                mAdapter.notifyDataSetChanged();

//                mMessagesList.scrollToPosition( messagesList.size() - 1 );

                mRefreshLayout.setRefreshing( false );

                mLinearLayout.scrollToPositionWithOffset( 20, 0 );

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child( "Messages" ).child( mCurrentUserId ).child( mNgoID );
        // we weant the last 10 messages ..
        Query messageQuery = messageRef.limitToLast( mCurrentPage * TOTAL_ITEMS_TO_LOAD );

        // change the mRootRef to messageQuery ...
        messageQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue( Message.class );

                itemPos++;

                if (itemPos == 1) {
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey; // <<<<<
                }

                // we add the message that we get to our message list ...
                messagesList.add( message );
                mAdapter.notifyDataSetChanged();
                // that means this is like the bottom of the recycler view ( total no. of items - 1 )
                mMessagesList.scrollToPosition( messagesList.size() - 1 );

                // once the data is added  stop the refresh swipe ..
                mRefreshLayout.setRefreshing( false );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty( message )) {

            String current_user_ref = "Messages/" + mCurrentUserId + "/" + mNgoID;
            String chat_user_ref = "Messages/" + mNgoID + "/" + mCurrentUserId;

            // we wanna create a push for messages
            DatabaseReference user_message_push = mRootRef.child( "Messages" )
                    .child( mCurrentUserId ).child( mNgoID ).push();

            String push_id = user_message_push.getKey();


            Map messageMap = new HashMap();
            messageMap.put( "message", message );
            messageMap.put( "seen", false );
            messageMap.put( "type", "text" );
            messageMap.put( "time", ServerValue.TIMESTAMP );
            messageMap.put( "from", mCurrentUserId );


            Map messageUserMap = new HashMap();
            messageUserMap.put( current_user_ref + "/" + push_id, messageMap );
            messageUserMap.put( chat_user_ref + "/" + push_id, messageMap );

            mChatMessageView.setText( "" );

            SendNotification notification = new SendNotification();
            notification.sendNotification( mNgoID, mMyUserName + " : " + message );

            mRootRef.updateChildren( messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d( "CHAT_LOG", databaseError.getMessage().toString() );
                    }
                }
            } );

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog( ChatActivity.this );
                mProgressDialog.setTitle( "Sending Image ... " );
                mProgressDialog.setMessage( "Please wait while send image" );
                mProgressDialog.setCanceledOnTouchOutside( false );
                mProgressDialog.show();

                // we will get the results as a data ... this going to be the image uri that we will get from our intent
                Uri imageUri = result.getUri();
                // start cropping activity for pre-acquired image saved on the device

                final String current_user_ref = "Messages/" + mCurrentUserId + "/" + mNgoID;
                final String ngo_ref = "Messages/" + mNgoID + "/" + mCurrentUserId;

                DatabaseReference user_message_push = mRootRef.child( "Messages" ).child( mCurrentUserId ).child( mNgoID ).push();
                final String push_id = user_message_push.getKey();
                final StorageReference filepath = mImageStorage.child( "message_images" ).child( push_id + ".jpg" );

                filepath.putFile( imageUri ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // to get the image URL and store it to current user data
                            filepath.getDownloadUrl().addOnSuccessListener( new OnSuccessListener <Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    download_url = uri.toString();
                                }
                            } );




                            Map messageMap = new HashMap();
                            messageMap.put( "message", download_url );  // >>> download_url
                            messageMap.put( "seen", false );
                            messageMap.put( "type", "image" );  // >>> image
                            messageMap.put( "time", ServerValue.TIMESTAMP );
                            messageMap.put( "from", mCurrentUserId );


                            Map messageUserMap = new HashMap();
                            messageUserMap.put( current_user_ref + "/" + push_id, messageMap );
                            messageUserMap.put( ngo_ref + "/" + push_id, messageMap );

                            mChatMessageView.setText( "" );

                            SendNotification notification = new SendNotification();
                            notification.sendNotification( mNgoID, mMyUserName + " : " + "Send a photo" );


                            mRootRef.updateChildren( messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    // progress dialog
                                    mProgressDialog.dismiss();

                                    if (databaseError != null) {

                                        Log.d( "CHAT_LOG", databaseError.getMessage().toString() );

                                    }
                                }
                            } );
                        }
                    }
                } );

            }

        } // add this to the close the if statement

    } //onActivity Result

}
