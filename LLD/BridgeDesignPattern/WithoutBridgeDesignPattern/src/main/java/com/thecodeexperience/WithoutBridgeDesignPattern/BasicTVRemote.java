package com.thecodeexperience.WithoutBridgeDesignPattern;

public class BasicTVRemote extends Remote {

    private boolean on = false;
    private int volume = 30;

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

}
