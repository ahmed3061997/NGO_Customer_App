package com.belal.projects.ngo.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.activities.MainActivity;
import com.belal.projects.ngo.models.Ngo;
import com.belal.projects.ngo.view_holders.NgoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NGOsFragment extends Fragment {
    private RecyclerView mNgoList ;
    private DatabaseReference mNgoDatabase ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_ngos, container, false);

        mNgoDatabase = FirebaseDatabase.getInstance().getReference().child( "Ngos" );

        // recycler view
        mNgoList = (RecyclerView) view.findViewById( R.id.ngo_recycler_view );
        // we need to make sure we set a fixed size
        mNgoList.setHasFixedSize( true );
        // and we also need layout manager ...
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mNgoList.setLayoutManager( new GridLayoutManager( getContext(), 2 ) );
        }else {
            mNgoList.setLayoutManager( new GridLayoutManager( getActivity(), 4 ) );
        }


        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Ngo> options = new FirebaseRecyclerOptions.Builder<Ngo>()
                .setQuery( mNgoDatabase, Ngo.class )
                .build();

        // firebase recycler adapter
        FirebaseRecyclerAdapter<Ngo, NgoViewHolder> NgoAdapter = new FirebaseRecyclerAdapter <Ngo, NgoViewHolder>(options) {
            @Override
            // that will be used to actually set the value to recycler view items
            protected void onBindViewHolder(@NonNull NgoViewHolder ngoViewHolder, int i, @NonNull Ngo ngo) {
                Picasso.get().load( ngo.getOrg_logo() ).placeholder( R.drawable.default_ngo_logo ).into( ngoViewHolder.mNgoImageView );
                // to get ngo id ...
                final String ngo_id = getRef( i ).getKey();
                ngoViewHolder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText( getActivity(),ngo_id,Toast.LENGTH_LONG ).show();
                    }
                } );

            }

            @NonNull
            @Override
            public NgoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.ngo_single_layout, parent, false );
                return new NgoViewHolder( view );
            }
        };

        mNgoList.setItemAnimator( new DefaultItemAnimator() );
        mNgoList.setAdapter( NgoAdapter );
        NgoAdapter.notifyDataSetChanged();
        NgoAdapter.startListening();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Organizations");
    }
}
