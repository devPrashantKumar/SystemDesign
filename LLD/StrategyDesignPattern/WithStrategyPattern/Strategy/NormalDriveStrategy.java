package LLD.StrategyDesignPattern.WithStrategyPattern.Strategy;

public class NormalDriveStrategy implements DriveStrategy {
    public void drive(){
        System.out.println("Normal Drive Capability");
    };
}
