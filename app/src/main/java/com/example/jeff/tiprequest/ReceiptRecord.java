package com.example.jeff.tiprequest;

import java.io.Serializable;
import com.google.android.gms.maps.model.LatLng;

public class ReceiptRecord implements Serializable {

    public String firebaseKey;

    private double subtotal = 0.0;
    private double total = 0.0;
    private double tipPercent = 0.0;
    private double tipAmount = 0.0;
    private String location = "";
    private LatLng locLatLng;
    private String time;


    // if location is available
    public ReceiptRecord(String firebaseKey, double subtotal, double total, double tipPercent, double tipAmount, String location, LatLng locLatLng, String time) {
        this.firebaseKey = firebaseKey;
        this.subtotal = subtotal;
        this.total = total;
        this.tipPercent = tipPercent;
        this.tipAmount = tipAmount;
        this.location = location;
        this.locLatLng = locLatLng;
        this.time = time;
    }
    public ReceiptRecord(double subtotal, double total, double tipPercent, double tipAmount, String location, LatLng locLatLng, String time) {
        this.firebaseKey = "";
        this.subtotal = subtotal;
        this.total = total;
        this.tipPercent = tipPercent;
        this.tipAmount = tipAmount;
        this.location = location;
        this.locLatLng = locLatLng;
        this.time = time;
    }
    public ReceiptRecord() {
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }
    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double id) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }

    public double getTipPercent() {
        return tipPercent;
    }
    public void setTipPercent(double tipPercent) {
        this.tipPercent = tipPercent;
    }

    public double getTipAmount() {
        return tipAmount;
    }
    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public LatLng getLocLatLng() {
        return locLatLng;
    }
    public void setLocLatLng(LatLng locLatLng) {
        this.locLatLng = locLatLng;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        String totalString = String.format("$%.2f", total);
        double tip = tipPercent * 100;
        return (totalString + " (" + tip + "% tip included)");
    }
}