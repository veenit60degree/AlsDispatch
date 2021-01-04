package com.als.dispatchNew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.models.TripHistoryModel;

import java.util.List;

public class TripAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<TripHistoryModel> tripList;
    String tripType;

    public TripAdapter(Context context, List<TripHistoryModel> transferList, String trip_type){
        this.context = context;
        this.tripList = transferList;
        this.tripType = trip_type;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tripList.size();
    }

    @Override
    public Object getItem(int position) {
        return tripList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final TripHistoryModel tripItem = tripList.get(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, null);

            holder.itemLoadTV = (TextView)convertView.findViewById(R.id.itemLoadTV);
            holder.itemDetailTV = (TextView)convertView.findViewById(R.id.itemDetailTV);
            holder.consigneeNameTV = (TextView)convertView.findViewById(R.id.consigneeNameTxtView);
            holder.container_name_tv = (TextView)convertView.findViewById(R.id.container_name_tv);
            holder.shiperNameV = (TextView)convertView.findViewById(R.id.shiperNameV);
            holder.shipperDetailTV = (TextView)convertView.findViewById(R.id.tripHisDetailTV);
            holder.tripDividerTV = (TextView)convertView.findViewById(R.id.tripDividerTV);
            holder.consigneNameTV = (TextView)convertView.findViewById(R.id.consigneNameTV);
            holder.timeTextView     = (TextView)convertView.findViewById(R.id.timeTextView);
            holder.deliveryTimeTextView = (TextView)convertView.findViewById(R.id.deliveryTimeTextView);
            holder.loadTypeTxtVw        = (TextView)convertView.findViewById(R.id.loadTypeTxtVw);

            holder.shiperNameV.setText(tripItem.getShipperName());
            holder.container_name_tv.setText("Trip no : " + tripItem.getTripNumber());
            holder.itemLoadTV.setText("Load : #"+tripItem.getLoadNumber());
            holder.consigneeNameTV.setText(tripItem.getConsigneeName());

            holder.timeTextView.setText(tripItem.getPickedDateTime());
            holder.deliveryTimeTextView.setText(tripItem.getDeliveredDateTime());




            String shipperDetailStr, consigneeDetailStr;
            if(tripType.equals("local_trip")){
              //  holder.consigneNameTV.setText("Yard : ");
                holder.container_name_tv.setVisibility(View.GONE);
                holder.tripDividerTV.setVisibility(View.GONE);
                consigneeDetailStr = tripItem.getConsigneeAddress();
                shipperDetailStr = tripItem.getShipperAddress()+", " + tripItem.getShipperStateCode()+", " + tripItem.getShipperCountryCode();

            }else{
                consigneeDetailStr = tripItem.getConsigneeAddress()+", " + tripItem.getConsigneeStateCode()+", " +
                        tripItem.getConsigneeCity()+ ", " + tripItem.getConsigneePostal()+", " +  tripItem.getConsigneeCountryCode();
                shipperDetailStr = tripItem.getShipperAddress()+", " + tripItem.getShipperStateCode()+", " +
                        tripItem.getShipperCity()+ ", " + tripItem.getShipperPostal()+", " +  tripItem.getShipperCountryCode();


            }

            holder.loadTypeTxtVw.setText(tripItem.getLoadStatusName());
            holder.itemDetailTV.setText(consigneeDetailStr);
            holder.shipperDetailTV.setText(shipperDetailStr);


            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }





        return convertView;
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
        TextView itemLoadTV, consigneNameTV, itemDetailTV, tripDividerTV, consigneeNameTV,
                container_name_tv, shiperNameV, shipperDetailTV, deliveryTimeTextView, timeTextView , loadTypeTxtVw ;

    }

}
