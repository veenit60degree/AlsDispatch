package com.als.dispatchNew.models;

public class NavigationMenuModel {

    int id;
    int itemDrawable;
    String itemName;

    public NavigationMenuModel(int id, int itemDrawable, String itemName) {
        this.id = id;
        this.itemDrawable = itemDrawable;
        this.itemName = itemName;
    }


    public int getId() {
        return id;
    }

    public int getItemDrawable() {
        return itemDrawable;
    }

    public String getItemName() {
        return itemName;
    }
}
