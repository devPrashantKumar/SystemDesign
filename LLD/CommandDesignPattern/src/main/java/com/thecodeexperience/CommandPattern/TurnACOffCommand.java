package com.thecodeexperience.CommandPattern;

public class TurnACOffCommand implements ICommand{
    AirConditioner airConditioner;

    TurnACOffCommand(AirConditioner airConditioner){
        this.airConditioner = airConditioner;
    }

    @Override
    public void execute() {
        airConditioner.turnOff();
    }

    @Override
    public void undo() {
        airConditioner.turnOn();
    }
}
