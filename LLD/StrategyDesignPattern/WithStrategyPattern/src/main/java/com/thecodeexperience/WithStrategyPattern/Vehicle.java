package com.thecodeexperience.WithStrategyPattern;

import com.thecodeexperience.WithStrategyPattern.Strategy.DriveStrategy;

import java.util.Objects;

/**
 * CONTEXT — owns a strategy and delegates to it.
 *
 * Vehicle has no idea HOW driving works. It only knows that something
 * implementing DriveStrategy will do it. Swap that object and the behaviour
 * changes, with no edit to this class.
 */
public class Vehicle {

    private DriveStrategy driveStrategy;

    public Vehicle(DriveStrategy driveStrategy) {
        this.driveStrategy = Objects.requireNonNull(driveStrategy, "driveStrategy must not be null");
    }

    /**
     * Behaviour can be changed at RUNTIME — this is the payoff of Strategy.
     * Without this setter you have dependency injection, not Strategy.
     */
    public void setDriveStrategy(DriveStrategy driveStrategy) {
        this.driveStrategy = Objects.requireNonNull(driveStrategy, "driveStrategy must not be null");
    }

    public void drive() {
        driveStrategy.drive();
    }

}
