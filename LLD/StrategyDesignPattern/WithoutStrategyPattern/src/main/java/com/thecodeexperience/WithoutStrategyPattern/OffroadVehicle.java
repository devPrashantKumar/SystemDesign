package com.thecodeexperience.WithoutStrategyPattern;

public class OffroadVehicle extends Vehicle {

    // ⚠ THE PROBLEM: byte-for-byte identical to SportsVehicle.drive().
    // Inheritance gave us no way to share this without also becoming a SportsVehicle.
    @Override
    public void drive() {
        System.out.println("Sports Drive Capability");
    }

}
