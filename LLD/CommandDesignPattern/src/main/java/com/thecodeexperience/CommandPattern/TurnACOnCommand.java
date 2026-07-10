package com.thecodeexperience.CommandPattern;

public class TurnACOnCommand implements ICommand{
    AirConditioner airConditioner;

    TurnACOnCommand(AirConditioner airConditioner){
        this.airConditioner = airConditioner;
    }

    @Override
    public void execute() {
        airConditioner.turnOn();
    }

    @Override
    public void undo() {
        airConditioner.turnOff();
    }
}
