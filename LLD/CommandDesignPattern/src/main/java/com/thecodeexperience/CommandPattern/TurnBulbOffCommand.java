package com.thecodeexperience.CommandPattern;

public class TurnBulbOffCommand implements ICommand{
    Bulb bulb;

    TurnBulbOffCommand(Bulb bulb){
        this.bulb = bulb;
    }

    @Override
    public void execute() {
        bulb.turnOff();
    }

    @Override
    public void undo() {
        bulb.turnOn();
    }
}
