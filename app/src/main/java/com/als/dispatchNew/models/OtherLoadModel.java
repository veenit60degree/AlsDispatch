package com.als.dispatchNew.models;

public class OtherLoadModel {

    String LoadId;
    String LoadNumber;

    public OtherLoadModel(String loadId, String loadNumber) {
        LoadId = loadId;
        LoadNumber = loadNumber;
    }

    public String getLoadId() {
        return LoadId;
    }

    public String getLoadNumber() {
        return LoadNumber;
    }
}
