package com.als.dispatchNew.models;

public class ExpenseTripModel {

    String TripId;
    String TripNumber;

    public ExpenseTripModel(String tripId, String tripNumber) {
        TripId = tripId;
        TripNumber = tripNumber;
    }

    public String getTripId() {
        return TripId;
    }

    public String getTripNumber() {
        return TripNumber;
    }
}
