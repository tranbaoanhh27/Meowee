package com.example.meowee;

import android.util.Log;

import java.util.ArrayList;

class Branch {
    private static final String TAG = "Branch";
    
    String name, phoneNumber, openTime, closeTime;
    double latitude, longitude;
    float rating;
    int H_open, M_open, H_close, M_close;

    public Branch() {}

    public Branch(String name, String phoneNumber, String openTime, String closeTime, double latitude, double longitude, float rating) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.H_open = Integer.parseInt(openTime.substring(0, 2));
        this.M_open = Integer.parseInt(openTime.substring(3, 5));
        Log.d(TAG, "Branch: OpenTime [" + this.name + "] \"" + H_open + ":" + M_open + "\"");
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.H_close = Integer.parseInt(closeTime.substring(0, 2));
        this.M_close = Integer.parseInt(closeTime.substring(3, 5));
        Log.d(TAG, "Branch: CloseTime [" + this.name + "] \"" + H_close + ":" + M_close + "\"");
        this.closeTime = closeTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Branch clone() {
        return new Branch(name, phoneNumber, openTime, closeTime, latitude, longitude, rating);
    }

    private boolean isOpening(int currentH, int currentM) {
        if (this.H_open < currentH && currentH < this.H_close) {
            return true;
        }
        else if (this.H_open == currentH) {
            return this.M_open <= currentM;
        }
        else if (this.H_close == currentM) {
            return this.M_close > currentM;
        }
        return false;
    }

    public ArrayList<String> getFormat(int currentH, int currentM) {
        ArrayList<String> res = new ArrayList<>();

        boolean isopening = isOpening(currentH, currentM);

        if (isopening) {
            res.add("Open");
            res.add("Closes " + this.closeTime);
        }
        else {
            res.add("Closed");
            res.add("Opens " + this.openTime);
        }

        return res;
    }
}