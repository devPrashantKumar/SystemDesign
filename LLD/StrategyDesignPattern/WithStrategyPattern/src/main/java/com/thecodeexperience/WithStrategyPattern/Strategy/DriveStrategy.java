package com.thecodeexperience.WithStrategyPattern.Strategy;

/**
 * STRATEGY — the one thing that varies, put behind an interface.
 *
 * Deliberately tiny. A Strategy interface is usually a single operation:
 * "here is a job, and here are the interchangeable ways of doing it".
 */
public interface DriveStrategy {

    void drive();

}
