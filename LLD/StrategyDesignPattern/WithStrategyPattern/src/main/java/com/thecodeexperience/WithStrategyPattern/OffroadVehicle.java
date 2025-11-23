package com.thecodeexperience.WithStrategyPattern;

import com.thecodeexperience.WithStrategyPattern.Strategy.SportsDriveStrategy;

public class OffroadVehicle extends Vehicle {
    public OffroadVehicle(){
        super(new SportsDriveStrategy());
    }
    
}