package com.example.cartrackr.model;

import java.io.Serializable;

public class Vehicle implements Serializable {
    String id;
    String make;
    String model;
    int year;
    String refreshToken;

    public Vehicle(String id, String make, String model, int year, String token) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.refreshToken = token;
    }

    @Override
    public String toString() {
        return year + ' ' + make + ' ' + model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
