package com.als.dispatchNew.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.models.DocumentModel;

import java.util.List;


public class AddDocumentAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    LayoutInflater inflater;
    List<DocumentModel> transferList;


    public AddDocumentAdapter(Context context, List<DocumentModel> transferList ) {
        this.context = context;
        this.transferList = transferList;
        mInflater = LayoutInflater.from(context);
      //  this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return transferList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return transferList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final DocumentModel transferItem = (DocumentModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_document, null);

            holder.addDocumentTV = (TextView) convertView.findViewById(R.id.addDocumentTV);
            holder.documentTitleTV = (TextView) convertView.findViewById(R.id.documentTitleTV);
            holder.isUploadedIV = (ImageView)convertView.findViewById(R.id.isUploadedIV);
            holder.commercialDocccLay = (RelativeLayout)convertView.findViewById(R.id.commercialDocccLay);

            holder.documentTitleTV.setText(transferItem.getJobId());

            if(transferItem.getJobLoadDocTypeId().equalsIgnoreCase("uploaded")){
                holder.isUploadedIV.setVisibility(View.VISIBLE);
                holder.addDocumentTV.setVisibility(View.INVISIBLE);
            }else {
                holder.isUploadedIV.setVisibility(View.GONE);
                holder.addDocumentTV.setVisibility(View.VISIBLE);
            }

            holder.commercialDocccLay.setTag(holder);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.commercialDocccLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(transferItem.getJobLoadDocTypeId().equalsIgnoreCase("uploaded")){
                    openPdf(transferItem.getFilePath());
                }else{

                }
            }
        });


        return convertView;
    }





    void openPdf(String file)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType( Uri.parse( file ), "application/pdf" );
        context.startActivity(intent);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    public class ViewHolder {
        TextView addDocumentTV, documentTitleTV;
        ImageView isUploadedIV;
        RelativeLayout commercialDocccLay;

    }
}