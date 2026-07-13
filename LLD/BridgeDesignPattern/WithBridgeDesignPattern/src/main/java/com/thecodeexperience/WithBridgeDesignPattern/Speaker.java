package com.thecodeexperience.WithBridgeDesignPattern;

/**
 * A third device, added later.
 *
 * Note what did NOT have to change: RemoteControl, AdvancedRemoteControl, TV, Radio.
 * One new class buys BOTH remotes for this device.
 */
public class Speaker implements Device {

    private boolean on = false;
    private int volume = 30;
    private int preset = 1;

    @Override
    public void enable() {
        on = true;
        System.out.println("Speaker : powering on the bluetooth amplifier");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("Speaker : powering off the bluetooth amplifier");
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
        System.out.println("Speaker : volume is now " + this.volume + " (woofer)");
    }

    @Override
    public int getChannel() {
        return preset;
    }

    @Override
    public void setChannel(int channel) {
        this.preset = channel;
        System.out.println("Speaker : switched to preset " + this.preset);
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
