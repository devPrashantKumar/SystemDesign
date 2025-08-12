package LLD.StrategyDesignPattern.WithStrategyPattern;

import LLD.StrategyDesignPattern.WithStrategyPattern.Strategy.NormalDriveStrategy;

public class GoodsVehicle extends Vehicle {

    public GoodsVehicle() {
        super(new NormalDriveStrategy());
    }
}
