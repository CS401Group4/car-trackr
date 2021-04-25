package com.example.cartrackr.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Tasks model used for retrieving Firestore data
 * @since April 2021
 * @author Group 4
 */
@IgnoreExtraProperties
public class Tasks {
    /**
     * String to hold service name
     */
    String serviceName;

    /**
     * String to hold service date
     */
    String serviceDate;

    /**
     * Default constructor
     */
    public Tasks() {}

    /**
     * Custom constructor
     * @param serviceName string variable of service name
     * @param serviceDate string variable of service date
     */
    public Tasks(String serviceName, String serviceDate) {
        this.serviceName = serviceName;
        this.serviceDate = serviceDate;
    }

    /**
     * Override method of toString
     * @return string containing service name and date
     */
    @Override
    public String toString() {
        return serviceName + " (Due: " + serviceDate + ")" ;
    }

    /**
     * Getter for service name
     * @return string of service name
     */
    public String getServiceName() {
            return serviceName;
    }

    /**
     * Setter for service name
     * @param serviceName string of service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Getter for service date
     * @return string of service date
     */
    public String getServiceDate() {
        return serviceDate;
    }

    /**
     * Setter for service date
     * @param serviceDate string of service date
     */
    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }
}
