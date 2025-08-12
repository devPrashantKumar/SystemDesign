package LLD.StrategyDesignPattern.WithStrategyPattern;

import LLD.StrategyDesignPattern.WithStrategyPattern.Strategy.SportsDriveStrategy;

public class OffroadVehicle extends Vehicle {
    public OffroadVehicle(){
        super(new SportsDriveStrategy());
    }
    
}