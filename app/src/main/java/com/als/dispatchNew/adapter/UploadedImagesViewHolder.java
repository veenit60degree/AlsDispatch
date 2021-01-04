package com.als.dispatchNew.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.als.dispatchNew.R;

public class UploadedImagesViewHolder extends RecyclerView.ViewHolder {

public ImageView uploadedImageView;

public UploadedImagesViewHolder(View v) {
        super(v);

    uploadedImageView = (ImageView) v.findViewById(R.id.uploadedImageView);

    }


}