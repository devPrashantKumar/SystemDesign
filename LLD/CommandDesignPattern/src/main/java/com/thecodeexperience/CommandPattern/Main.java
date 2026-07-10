package com.thecodeexperience.CommandPattern;

public class Main {
    public static void main(String[] args) {
        AirConditioner airConditioner = new AirConditioner();
        Bulb bulb = new Bulb();

        TurnACOnCommand turnACOnCommand= new TurnACOnCommand(airConditioner);
        TurnACOffCommand turnACOffCommand = new TurnACOffCommand(airConditioner);
        TurnBulbOnCommand turnBulbOnCommand = new TurnBulbOnCommand(bulb);
        TurnBulbOffCommand turnBulbOffCommand = new TurnBulbOffCommand(bulb);

        MyRemoteControl myRemoteControl = new MyRemoteControl();
        myRemoteControl.setCommand("turnACOnCommand", turnACOnCommand);
        myRemoteControl.setCommand("turnACOffCommand", turnACOffCommand);
        myRemoteControl.setCommand("turnBulbOnCommand", turnBulbOnCommand);
        myRemoteControl.setCommand("turnBulbOffCommand", turnBulbOffCommand);

        myRemoteControl.pressButton("turnACOnCommand");
        myRemoteControl.undo();
        myRemoteControl.undo();
        System.out.println("---------------------------------------");
        myRemoteControl.pressButton("turnBulbOnCommand");
        myRemoteControl.pressButton("turnBulbOffCommand");
        myRemoteControl.pressButton("turnACOnCommand");
        myRemoteControl.undo();
        myRemoteControl.undo();
        myRemoteControl.undo();
        myRemoteControl.undo();
        System.out.println("---------------------------------------");
    }
}
