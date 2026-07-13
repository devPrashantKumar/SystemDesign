package com.thecodeexperience.WithBridgeDesignPattern;

public class Radio implements Device {

    private boolean on = false;
    private int volume = 30;
    private int station = 1;

    @Override
    public void enable() {
        on = true;
        System.out.println("Radio : powering on the tuner");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("Radio : powering off the tuner");
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
        System.out.println("Radio : volume is now " + this.volume + " (amplifier)");
    }

    @Override
    public int getChannel() {
        return station;
    }

    @Override
    public void setChannel(int channel) {
        this.station = channel;
        System.out.println("Radio : tuned to preset station " + this.station);
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
