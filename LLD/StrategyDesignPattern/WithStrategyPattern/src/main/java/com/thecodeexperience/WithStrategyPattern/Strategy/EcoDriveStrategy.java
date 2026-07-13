package com.thecodeexperience.WithStrategyPattern.Strategy;

/**
 * A third strategy, added later.
 *
 * Note what did NOT change to make room for it: Vehicle, and every existing
 * strategy. That is the Open/Closed Principle actually paying out.
 */
public class EcoDriveStrategy implements DriveStrategy {

    @Override
    public void drive() {
        System.out.println("Eco Drive Capability");
    }

}
