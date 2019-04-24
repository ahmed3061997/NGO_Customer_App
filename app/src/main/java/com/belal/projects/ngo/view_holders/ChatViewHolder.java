package com.belal.projects.ngo.view_holders;

import android.view.View;
import android.widget.TextView;

import com.belal.projects.ngo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

// view holder
public class ChatViewHolder extends RecyclerView.ViewHolder {
    // we need view that will be used by firebase adapter ...
    public CircleImageView mChatImageView ;
    public TextView mNgoName ;

    public ChatViewHolder(@NonNull View itemView) {
        super( itemView );
        mChatImageView = (CircleImageView) itemView.findViewById( R.id.single_conv_image );
        mNgoName = itemView.findViewById( R.id.single_conv_name );
    }
}
