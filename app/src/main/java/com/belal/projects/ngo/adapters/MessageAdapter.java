package com.belal.projects.ngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private FirebaseAuth mAuth ;
    private DatabaseReference mNgoDatabase;



    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }



    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        Message message = mMessageList.get( position );
        String from_user = message.getFrom();
        String message_type = message.getType();

        if (current_user_id.equals( from_user ) ) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT ;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED ;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message_single_layout, parent, false);
            return new SentMessageHolder(view);

        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView mMessageTxt ;
        ImageView mMessageImage ;
        SentMessageHolder(View itemView) {
            super(itemView);
            mMessageTxt = (TextView) itemView.findViewById( R.id.my_message_txt_layout );
            mMessageImage = (ImageView) itemView.findViewById( R.id.my_message_image_layout );

        }

        void bind(Message message) {
            String message_type = message.getType();
            if(message_type.equals( "text" )) {
                mMessageTxt.setVisibility( View.VISIBLE );
                mMessageImage.setVisibility( View.GONE );
                mMessageTxt.setText( message.getMessage() );

            }else {
                mMessageTxt.setVisibility( View.GONE );
                mMessageImage.setVisibility( View.VISIBLE );
                Picasso.get().load( message.getMessage() ).into( mMessageImage );
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        public CircleImageView mNgoLogo ;
        public TextView mMessageTxt ;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            mNgoLogo = (CircleImageView) itemView.findViewById(R.id.message_image_layout);
            mMessageTxt = (TextView) itemView.findViewById( R.id.message_txt_layout );
        }

        void bind(Message message) {
            mMessageTxt.setText(message.getMessage());
            String from_ngo = message.getFrom();
            String message_type = message.getType();

            mAuth = FirebaseAuth.getInstance();
            mNgoDatabase = FirebaseDatabase.getInstance().getReference().child( "Ngos" ).child( from_ngo );
            mNgoDatabase.keepSynced( true ); // <<<<< for offline capabilities

            mNgoDatabase.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String ngoLogo = dataSnapshot.child( "org_logo" ).getValue().toString();
                    Picasso.get().load( ngoLogo ).into( mNgoLogo );

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            } );

        }
    }
}