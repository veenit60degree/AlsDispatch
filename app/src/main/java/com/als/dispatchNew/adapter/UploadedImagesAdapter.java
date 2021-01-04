package com.als.dispatchNew.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.models.UploadedImageModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class UploadedImagesAdapter extends RecyclerView.Adapter<UploadedImagesViewHolder> {
    List<UploadedImageModel> imageList;
    private DisplayImageOptions options;
    ImageLoader imageLoader;
    Context context;


    public UploadedImagesAdapter( Context context, List<UploadedImageModel> Data) {
        this.context = context;
        imageList = Data;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.dummy_doc)
                .showImageForEmptyUri(R.drawable.dummy_doc)
                .showImageOnFail(R.drawable.dummy_doc)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();



        imageLoader = ImageLoader.getInstance();


    }
    @Override
    public UploadedImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_uploaded_images, parent, false);
        UploadedImagesViewHolder holder = new UploadedImagesViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final UploadedImagesViewHolder holder, final int position) {

        imageLoader.displayImage(imageList.get(position).getImageUrl(), holder.uploadedImageView, options);
        holder.uploadedImageView.setTag(imageList.get(position).getLoadDeliveryDocumentId());

        ((UploadedImagesViewHolder) holder).uploadedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    showFullScreenImage(position);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });




    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }


    void showFullScreenImage(int position){
        final Dialog picker = new Dialog(context);
        picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.popup_touch_imageview);

        ImageView touchImageView = (ImageView) picker.findViewById(R.id.touchImageView);
        Button closeBtn = (Button) picker.findViewById(R.id.closeBtn);

        imageLoader.displayImage(imageList.get(position).getImageUrl(), touchImageView, options);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.dismiss();
            }
        });



        picker.show();
    }


}