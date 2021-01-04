package com.als.dispatchNew.models;

public class UploadedImageModel {

    int LoadDeliveryDocumentId;
    String imageUrl;

    public UploadedImageModel(int loadDeliveryDocumentId, String imageUrl) {
        LoadDeliveryDocumentId = loadDeliveryDocumentId;
        this.imageUrl = imageUrl;

    }


    public int getLoadDeliveryDocumentId() {
        return LoadDeliveryDocumentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
