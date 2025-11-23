package com.thecodeexperience.WithStrategyPattern;

import com.thecodeexperience.WithStrategyPattern.Strategy.NormalDriveStrategy;

public class PassengerVehicle extends Vehicle {
    public PassengerVehicle(){
        super(new NormalDriveStrategy());
    }
    
}
