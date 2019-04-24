package com.belal.projects.ngo.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.activities.MainActivity;
import com.belal.projects.ngo.activities.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MoreFragment extends Fragment {
    // firebase Auth
    private FirebaseAuth mAuth ;
    // logout textView
    private TextView mLogOut ;
    private TextView mAddNgo ;
    private DatabaseReference mDatabase ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_more, container, false);

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Ngos" ).child( "8" );

        mAddNgo = view.findViewById( R.id.add_btn );
        mAddNgo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> ngoMap = new HashMap <>(  );
                ngoMap.put( "org_name", "SAVE" );
                ngoMap.put( "org_logo", "https://firebasestorage.googleapis.com/v0/b/ngoproject-de7ca.appspot.com/o/organization_images%2FLogos%2FSAVE.jpg?alt=media&token=0f2d36ec-bc0f-4f38-96bc-962f08905117" );
                ngoMap.put( "org_about", "default" );
                ngoMap.put( "cover_image", "default" );
                ngoMap.put( "facebook_page", "default" );
                ngoMap.put( "mobile_number", "default" );
                ngoMap.put( "email_address", "default" );
                ngoMap.put( "website_link", "default" );
                mDatabase.setValue( ngoMap );
            }
        } );



        // logout textView
        mLogOut = (TextView) view.findViewById( R.id.more_logout_txt );
        mLogOut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options [] = new CharSequence[]{"I'm Sure", "Cancel"};
                final AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                builder.setTitle("Are you sure , you want to log out ?!");
                builder.setItems( options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0 :
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                // signout
                                if(currentUser != null && currentUser.isAnonymous()){
                                    currentUser.delete();
                                }else {
                                    mAuth.signOut();
                                }

                                Intent startIntent = new Intent( getActivity(), StartActivity.class );
                                startActivity( startIntent );
                                getActivity().finish();

                                Toast.makeText( getActivity(), "Log Out Successfuly !", Toast.LENGTH_SHORT ).show();
                                break;

                            case 1 :
                                dialog.dismiss();
                        }
                    }
                } );

                builder.show();


            }
        } );

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("More");
    }
}
