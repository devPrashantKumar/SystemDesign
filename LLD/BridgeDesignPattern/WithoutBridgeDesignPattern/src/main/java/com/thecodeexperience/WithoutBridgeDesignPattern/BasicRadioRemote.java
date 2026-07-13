package com.thecodeexperience.WithoutBridgeDesignPattern;

public class BasicRadioRemote extends Remote {

    private boolean on = false;
    private int volume = 30;

    @Override
    public void turnOn() {
        on = true;
        System.out.println("Radio : powering on the tuner");
    }

    @Override
    public void turnOff() {
        on = false;
        System.out.println("Radio : powering off the tuner");
    }

    @Override
    public void volumeUp() {
        volume += 10;
        System.out.println("Radio : volume is now " + volume + " (amplifier)");
    }

}
