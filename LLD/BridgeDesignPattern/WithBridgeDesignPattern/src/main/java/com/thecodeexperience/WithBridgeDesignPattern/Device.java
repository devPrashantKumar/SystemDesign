package com.thecodeexperience.WithBridgeDesignPattern;

/**
 * IMPLEMENTOR — what a device can physically do.
 *
 * Deliberately primitive: power, volume, channel. It knows nothing about
 * remotes, buttons, or how a user drives it.
 */
public interface Device {

    void enable();

    void disable();

    boolean isEnabled();

    int getVolume();

    void setVolume(int volume);

    int getChannel();

    void setChannel(int channel);

}
