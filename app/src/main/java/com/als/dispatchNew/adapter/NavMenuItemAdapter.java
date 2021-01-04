package com.als.dispatchNew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.als.dispatchNew.R;
import com.als.dispatchNew.models.NavigationMenuModel;

import java.util.List;

public class NavMenuItemAdapter  extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<NavigationMenuModel> menuList;

    public NavMenuItemAdapter(Context context, List<NavigationMenuModel> transferList){
        this.context = context;
        this.menuList = transferList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final NavigationMenuModel menuItem = (NavigationMenuModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_navigation_menu, null);

            holder.navMenuTxtVw = (TextView)convertView.findViewById(R.id.navMenuTxtVw);
            holder.navMenuImgBtn = (ImageView) convertView.findViewById(R.id.navMenuImgBtn);

            holder.navMenuTxtVw.setText(menuItem.getItemName());
            holder.navMenuImgBtn.setImageResource(menuItem.getItemDrawable());

            if(position == 7){
                holder.navMenuImgBtn.setVisibility(View.GONE);
            }
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
        TextView navMenuTxtVw ;
        ImageView navMenuImgBtn;

    }

}
