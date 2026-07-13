package com.thecodeexperience.WithBridgeDesignPattern;

public class TV implements Device {

    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public void enable() {
        on = true;
        System.out.println("TV : powering on the screen and the backlight");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("TV : powering off the screen and the backlight");
    }

    @Override
    public boolean isEnabled() {
        return on;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        this.volume = clamp(volume);
        System.out.println("TV : volume is now " + this.volume + " (speakers)");
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("TV : tuned to channel " + this.channel);
    }

    private int clamp(int volume) {
        if (volume < 0) {
            return 0;
        }
        if (volume > 100) {
            return 100;
        }
        return volume;
    }

}
