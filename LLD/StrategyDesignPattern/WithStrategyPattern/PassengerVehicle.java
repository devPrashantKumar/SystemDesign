package LLD.StrategyDesignPattern.WithStrategyPattern;

import LLD.StrategyDesignPattern.WithStrategyPattern.Strategy.NormalDriveStrategy;

public class PassengerVehicle extends Vehicle {
    public PassengerVehicle(){
        super(new NormalDriveStrategy());
    }
    
}
