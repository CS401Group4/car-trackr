package com.example.cartrackr.model;

import junit.framework.TestCase;

/**
 * Unit Test for Vehicle model
 * @since April 2021
 * @author Group 4
 */
public class VehicleTest extends TestCase {

    /**
     * Test for toString override method
     */
    public void testTestToString() {
    }

    /**
     * Test for id getter
     */
    public void testGetId() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getId(), "1");
    }

    /**
     * Test for id setter
     */
    public void testSetId() {
        Vehicle vehicle = new Vehicle();
        String id = "1";
        vehicle.setId(id);
        assertEquals(vehicle.getId(), id);
    }

    /**
     * Test for make getter
     */
    public void testGetMake() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getMake(), "Toyota");
    }

    /**
     * Test for make setter
     */
    public void testSetMake() {
        Vehicle vehicle = new Vehicle();
        String make = "Toyota";
        vehicle.setMake(make);
        assertEquals(vehicle.getMake(), make);
    }

    /**
     * Test for model getter
     */
    public void testGetModel() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getModel(), "Corolla");
    }

    /**
     * Test for model setter
     */
    public void testSetModel() {
        Vehicle vehicle = new Vehicle();
        String model = "Corolla";
        vehicle.setModel(model);
        assertEquals(vehicle.getModel(), model);
    }

    /**
     * Test for year getter
     */
    public void testGetYear() {
        Vehicle vehicle = new Vehicle("1", "Toyota", "Corolla", 2021, "sampleToken");
        assertEquals(vehicle.getYear(), 2021);
    }

    /**
     * Test for year setter
     */
    public void testSetYear() {
        Vehicle vehicle = new Vehicle();
        int year = 2021;
        vehicle.setYear(year);
        assertEquals(vehicle.getYear(), year);
    }
}