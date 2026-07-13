package com.thecodeexperience.WithoutBridgeDesignPattern;

public class AdvancedTVRemote extends Remote {

    private boolean on = false;
    private int volume = 30;

    // ⚠ every line below is copy-pasted from BasicTVRemote
    @Override
    public void turnOn() {
        on = true;
        System.out.println("TV : powering on the screen and the backlight");
    }

    @Override
    public void turnOff() {
        on = false;
        System.out.println("TV : powering off the screen and the backlight");
    }

    @Override
    public void volumeUp() {
        volume += 10;
        System.out.println("TV : volume is now " + volume + " (speakers)");
    }

    // the only genuinely new behaviour in this class
    public void mute() {
        volume = 0;
        System.out.println("TV : volume is now " + volume + " (speakers)");
    }

}
