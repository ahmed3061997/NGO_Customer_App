package com.belal.projects.ngo.view_holders;

import android.view.View;
import android.widget.TextView;

import com.belal.projects.ngo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

// view holder
public class NgoViewHolder extends RecyclerView.ViewHolder {
    // we need view that will be used by firebase adapter ...
    public CircleImageView mNgoImageView ;
    public TextView mNgoName ;

    public NgoViewHolder(@NonNull View itemView) {
        super( itemView );
        mNgoImageView = (CircleImageView) itemView.findViewById( R.id.single_ngo_logo );
        mNgoName = itemView.findViewById( R.id.single_conv_name );
    }
}
