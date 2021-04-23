package com.example.cartrackr.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Tasks {
    String serviceName;
    String serviceDate;

    public Tasks() {}

    public Tasks(String serviceName, String serviceDate) {
        this.serviceName = serviceName;
        this.serviceDate = serviceDate;
    }

    @Override
    public String toString() {
        return serviceName + ' ' + serviceDate;
    }

    public String getServiceName() {
            return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }
}
