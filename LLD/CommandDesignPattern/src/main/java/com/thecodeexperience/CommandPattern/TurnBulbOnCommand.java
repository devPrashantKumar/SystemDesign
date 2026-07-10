package com.thecodeexperience.CommandPattern;

public class TurnBulbOnCommand implements ICommand{
    Bulb bulb;

    TurnBulbOnCommand(Bulb bulb){
        this.bulb = bulb;
    }

    @Override
    public void execute() {
        bulb.turnOn();
    }

    @Override
    public void undo() {
        bulb.turnOff();
    }
}
