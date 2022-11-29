package com.example.meowee;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        Log.d(TAG, "Branch: OpenTime [" + this.name + "] \"" + Integer.toString(H_open) + ":" + Integer.toString(M_open) + "\"");
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.H_close = Integer.parseInt(closeTime.substring(0, 2));
        this.M_close = Integer.parseInt(closeTime.substring(3, 5));
        Log.d(TAG, "Branch: CloseTime [" + this.name + "] \"" + Integer.toString(H_close) + ":" + Integer.toString(M_close) + "\"");
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
            if (this.M_open <= currentM)
                return true;
        }
        else if (this.H_close == currentM) {
            if (this.M_close > currentM)
                return true;
        }
        return false;
    }

    public ArrayList<String> getFormat(int currentH, int currentM) {
        ArrayList<String> res = new ArrayList<>();

        boolean isopening = isOpening(currentH, currentM);

        if (isopening) {
            res.add(String.format("Open"));
            res.add(String.format("Closes " + this.closeTime));
        }
        else {
            res.add(String.format("Closed"));
            res.add(String.format("Opens " + this.openTime));
        }

        return res;
    }
}

//currentUserDatabaseRef