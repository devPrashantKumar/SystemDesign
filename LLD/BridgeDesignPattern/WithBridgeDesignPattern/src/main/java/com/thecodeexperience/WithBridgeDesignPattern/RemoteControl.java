package com.thecodeexperience.WithBridgeDesignPattern;

/**
 * ABSTRACTION — what a user can do with a remote.
 *
 * It owns a reference to a Device and delegates every physical action to it.
 * That reference IS the bridge: the two hierarchies meet here and nowhere else.
 */
public class RemoteControl {

    protected Device device;   // the BRIDGE — composition, not inheritance

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }

    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
    }

    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
    }

    public void channelUp() {
        device.setChannel(device.getChannel() + 1);
    }

    public void channelDown() {
        device.setChannel(device.getChannel() - 1);
    }

}
