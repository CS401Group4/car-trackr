package com.example.cartrackr.model;

import com.google.android.gms.tasks.Task;

import junit.framework.TestCase;

/**
 * Unit tests for Tasks Model
 * @since April 2021
 * @author Group 4
 */
public class TasksTest extends TestCase {

    /**
     * Test for toString override method
     */
    public void testTestToString() {
    }

    /**
     * Test for service name getter
     */
    public void testGetServiceName() {
        Tasks tasks = new Tasks("Change oil", "5/1/2021");
        assertEquals(tasks.getServiceName(), "Change oil");
    }

    /**
     * Second Test for service name getter
     */
    public void testGetServiceName2() {
        Tasks tasks = new Tasks("Car wash", "5/1/2021");
        assertEquals(tasks.getServiceName(), "Car wash");
    }

    /**
     * Test for service name setter
     */
    public void testSetServiceName() {
        Tasks tasks = new Tasks();
        String taskName = "Tire change";
        tasks.setServiceName(taskName);
        assertEquals(tasks.getServiceName(), taskName);
    }

    /**
     * Second Test for service name setter
     */
    public void testSetServiceName2() {
        Tasks tasks = new Tasks();
        String taskName = "Change oil";
        tasks.setServiceName(taskName);
        assertEquals(tasks.getServiceName(), taskName);
    }

    /**
     * Test for service date getter
     */
    public void testGetServiceDate() {
        Tasks tasks = new Tasks("Car wash", "5/1/2021");
        assertEquals(tasks.getServiceDate(), "5/1/2021");
    }

    /**
     * Second Test for service date getter
     */
    public void testGetServiceDate2() {
        Tasks tasks = new Tasks("Change oil", "6/1/2021");
        assertEquals(tasks.getServiceDate(), "6/1/2021");
    }

    /**
     * Test for service date setter
     */
    public void testSetServiceDate() {
        Tasks tasks = new Tasks();
        String taskDate = "5/5/2021";
        tasks.setServiceDate(taskDate);
        assertEquals(tasks.getServiceDate(), taskDate);
    }

    /**
     * Second Test for service date setter
     */
    public void testSetServiceDate2() {
        Tasks tasks = new Tasks();
        String taskDate = "6/1/2023";
        tasks.setServiceDate(taskDate);
        assertEquals(tasks.getServiceDate(), taskDate);
    }
}