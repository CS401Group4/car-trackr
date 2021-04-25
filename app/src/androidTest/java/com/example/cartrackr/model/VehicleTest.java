package com.example.cartrackr.model;

import junit.framework.TestCase;

public class VehicleTest extends TestCase {

    public void testTestToString() {
    }

    public void testGetId() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getId(), "1");
    }

    public void testSetId() {
        Vehicle vehicle = new Vehicle();
        String id = "1";
        vehicle.setId(id);
        assertEquals(vehicle.getId(), id);
    }

    public void testGetMake() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getMake(), "Toyota");
    }

    public void testSetMake() {
        Vehicle vehicle = new Vehicle();
        String make = "Toyota";
        vehicle.setMake(make);
        assertEquals(vehicle.getMake(), make);
    }

    public void testGetModel() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getModel(), "Corolla");
    }

    public void testSetModel() {
        Vehicle vehicle = new Vehicle();
        String model = "Corolla";
        vehicle.setModel(model);
        assertEquals(vehicle.getModel(), model);
    }

    public void testGetYear() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getYear(), 2021);
    }

    public void testSetYear() {
        Vehicle vehicle = new Vehicle();
        int year = 2021;
        vehicle.setYear(year);
        assertEquals(vehicle.getYear(), year);
    }
}