package LLD.StrategyDesignPattern.WithStrategyPattern;

import LLD.StrategyDesignPattern.WithStrategyPattern.Strategy.SportsDriveStrategy;

public class SportsVehicle extends Vehicle {
    
    public SportsVehicle(){
        super(new SportsDriveStrategy());
    }

}
