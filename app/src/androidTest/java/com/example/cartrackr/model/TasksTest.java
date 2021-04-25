package com.example.cartrackr.model;

import com.google.android.gms.tasks.Task;

import junit.framework.TestCase;

public class TasksTest extends TestCase {

    public void testTestToString() {
    }

    public void testGetServiceName() {
        Tasks tasks = new Tasks("Change oil", "5/1/2021");
        assertEquals(tasks.getServiceName(), "Change oil");
    }

    public void testGetServiceName2() {
        Tasks tasks = new Tasks("Car wash", "5/1/2021");
        assertEquals(tasks.getServiceName(), "Car wash");
    }

    public void testSetServiceName() {
        Tasks tasks = new Tasks();
        String taskName = "Tire change";
        tasks.setServiceName(taskName);
        assertEquals(tasks.getServiceName(), taskName);
    }

    public void testSetServiceName2() {
        Tasks tasks = new Tasks();
        String taskName = "Change oil";
        tasks.setServiceName(taskName);
        assertEquals(tasks.getServiceName(), taskName);
    }

    public void testGetServiceDate() {
        Tasks tasks = new Tasks("Car wash", "5/1/2021");
        assertEquals(tasks.getServiceDate(), "5/1/2021");
    }

    public void testGetServiceDate2() {
        Tasks tasks = new Tasks("Change oil", "6/1/2021");
        assertEquals(tasks.getServiceDate(), "6/1/2021");
    }

    public void testSetServiceDate() {
        Tasks tasks = new Tasks();
        String taskDate = "5/5/2021";
        tasks.setServiceDate(taskDate);
        assertEquals(tasks.getServiceDate(), taskDate);
    }

    public void testSetServiceDate2() {
        Tasks tasks = new Tasks();
        String taskDate = "6/1/2023";
        tasks.setServiceDate(taskDate);
        assertEquals(tasks.getServiceDate(), taskDate);
    }
}