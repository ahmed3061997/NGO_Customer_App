package com.belal.projects.ngo.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class MoreFragment extends Fragment {
    // firebase Auth
    private FirebaseAuth mAuth ;
    // logout textView
    private TextView mLogOut ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_more, container, false);

        // firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
