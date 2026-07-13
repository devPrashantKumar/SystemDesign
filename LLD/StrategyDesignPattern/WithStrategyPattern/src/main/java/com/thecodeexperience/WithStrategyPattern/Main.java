package com.thecodeexperience.WithStrategyPattern;

import com.thecodeexperience.WithStrategyPattern.Strategy.EcoDriveStrategy;
import com.thecodeexperience.WithStrategyPattern.Strategy.NormalDriveStrategy;
import com.thecodeexperience.WithStrategyPattern.Strategy.SportsDriveStrategy;

public class Main {

    public static void main(String[] args) {

        // 1. The same four vehicles as the "Without" version — same output,
        //    but the drive code now lives in ONE place per capability.
        System.out.println("--- the four vehicles ---");
        SportsVehicle sportsVehicle = new SportsVehicle();
        sportsVehicle.drive();

        PassengerVehicle passengerVehicle = new PassengerVehicle();
        passengerVehicle.drive();

        OffroadVehicle offroadVehicle = new OffroadVehicle();   // shares SportsDriveStrategy — no duplication
        offroadVehicle.drive();

        GoodsVehicle goodsVehicle = new GoodsVehicle();
        goodsVehicle.drive();

        // 2. THE POINT OF STRATEGY: behaviour changes at runtime.
        //    Same object, same reference — different capability.
        System.out.println();
        System.out.println("--- the sports car switches mode, mid-drive ---");
        sportsVehicle.drive();
        sportsVehicle.setDriveStrategy(new EcoDriveStrategy());   // added later; Vehicle never changed
        sportsVehicle.drive();
        sportsVehicle.setDriveStrategy(new SportsDriveStrategy());
        sportsVehicle.drive();

        // 3. The subclasses are only a convenience — they pre-select a strategy.
        //    A Vehicle can be built directly with any capability you like.
        System.out.println();
        System.out.println("--- no subclass needed ---");
        Vehicle plainVehicle = new Vehicle(new NormalDriveStrategy());
        plainVehicle.drive();
    }

}
