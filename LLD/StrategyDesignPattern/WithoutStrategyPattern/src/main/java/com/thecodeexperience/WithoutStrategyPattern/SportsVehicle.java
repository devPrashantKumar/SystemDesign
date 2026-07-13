package com.thecodeexperience.WithoutStrategyPattern;

public class SportsVehicle extends Vehicle {

    // ⚠ THE PROBLEM: OffroadVehicle needs this exact behaviour too,
    // and the only way to get it is to copy-paste the method.
    @Override
    public void drive() {
        System.out.println("Sports Drive Capability");
    }

}
