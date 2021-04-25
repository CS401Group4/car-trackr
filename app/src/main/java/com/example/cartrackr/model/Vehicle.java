package com.example.cartrackr.model;

import java.io.Serializable;

/**
 * Vehicle model class
 * @since April 2021
 * @author Group 4
 */
public class Vehicle implements Serializable {
    /**
     * String of vehicle id
     */
    String id;
    /**
     * String of vehicle make
     */
    String make;
    /**
     * String of vehicle model
     */
    String model;
    /**
     * String of vehicle year
     */
    int year;
    /**
     * String of refresh token
     */
    String refreshToken;

    /**
     * Custom constructor
     * @param id string of vehicle id
     * @param make string of vehicle make
     * @param model string of vehicle model
     * @param year string of vehicle year
     * @param token string of access token
     */
    public Vehicle(String id, String make, String model, int year, String token) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.refreshToken = token;
    }

    /**
     * Override method of toString
     * @return string of vehicle year make and model
     */
    @Override
    public String toString() {
        return year + ' ' + make + ' ' + model;
    }

    /**
     * Getter for vehicle id
     * @return string of vehicle id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for vehicle id
     * @param id string of vehicle id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for vehicle make
     * @return string of vehicle make
     */
    public String getMake() {
        return make;
    }

    /**
     * Setter for vehicle make
     * @param make string of vehicle make
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Getter for vehicle model
     * @return string of vehicle model
     */
    public String getModel() {
        return model;
    }

    /**
     * Setter for vehicle model
     * @param model string of vehicle model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Getter for vehicle year
     * @return string of year
     */
    public int getYear() {
        return year;
    }

    /**
     * Setter for vehicle year
     * @param year string of vehicle year
     */
    public void setYear(int year) {
        this.year = year;
    }
}
