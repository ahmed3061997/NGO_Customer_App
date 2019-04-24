package com.belal.projects.ngo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.activities.ChatActivity;
import com.belal.projects.ngo.activities.MainActivity;
import com.belal.projects.ngo.models.Ngo;
import com.belal.projects.ngo.view_holders.ChatViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment {
    private RecyclerView mChatList ;
    private DatabaseReference mChatDatabase ;
    private DatabaseReference mNgoDatabase ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_chat, container, false);

        mChatDatabase = FirebaseDatabase.getInstance().getReference().child( "Ngos" );

        // recycler view
        mChatList = (RecyclerView) view.findViewById( R.id.chat_recycler_view );
        // we need to make sure we set a fixed size
        mChatList.setHasFixedSize( true );
        // and we also need layout manager ...
        mChatList.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Ngo> options = new FirebaseRecyclerOptions.Builder<Ngo>()
                .setQuery( mChatDatabase, Ngo.class )
                .build();

        // firebase recycler adapter
        FirebaseRecyclerAdapter<Ngo, ChatViewHolder> ChatAdapter = new FirebaseRecyclerAdapter <Ngo, ChatViewHolder>(options) {
            @Override
            // that will be used to actually set the value to recycler view items
            protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull Ngo ngo) {
                Picasso.get().load( ngo.getOrg_logo() ).placeholder( R.drawable.default_ngo_logo ).into( chatViewHolder.mChatImageView );
                chatViewHolder.mNgoName.setText( ngo.getOrg_name() );

                // to get ngo id ...
                final String ngo_id = getRef( i ).getKey();

                chatViewHolder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent chatIntent = new Intent( getActivity(), ChatActivity.class );
                        chatIntent.putExtra( "ngo_id", ngo_id );
                        startActivity( chatIntent );
                    }
                } );

            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.chat_single_layout, parent, false );
                return new ChatViewHolder( view );
            }
        };

        mChatList.setAdapter( ChatAdapter );
        ChatAdapter.notifyDataSetChanged();
        ChatAdapter.startListening();    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Chat");
    }
}
