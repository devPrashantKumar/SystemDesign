package com.thecodeexperience.WithStrategyPattern;

public class Main {
    public static void main(String[] args) {
        SportsVehicle sportsVehicle = new SportsVehicle();
        sportsVehicle.drive();

        PassengerVehicle passengerVehicle = new PassengerVehicle();
        passengerVehicle.drive();

        OffroadVehicle offroadVehicle = new OffroadVehicle();
        offroadVehicle.drive();

        GoodsVehicle goodsVehicle = new GoodsVehicle();
        goodsVehicle.drive();


    }
}
