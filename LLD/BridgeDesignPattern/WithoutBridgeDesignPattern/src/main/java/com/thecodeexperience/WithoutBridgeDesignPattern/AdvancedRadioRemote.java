package com.thecodeexperience.WithoutBridgeDesignPattern;

public class AdvancedRadioRemote extends Remote {

    private boolean on = false;
    private int volume = 30;

    // ⚠ every line below is copy-pasted from BasicRadioRemote
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

    // ⚠ and mute() is copy-pasted from AdvancedTVRemote, with one word changed
    public void mute() {
        volume = 0;
        System.out.println("Radio : volume is now " + volume + " (amplifier)");
    }

}
