package com.thecodeexperience.WithBridgeDesignPattern;

/**
 * REFINED ABSTRACTION — extends what the user can do, not what the device can do.
 *
 * Written once. It works with TV, Radio, and every device added in future,
 * because it only ever talks to the Device interface.
 */
public class AdvancedRemoteControl extends RemoteControl {

    public AdvancedRemoteControl(Device device) {
        super(device);
    }

    public void mute() {
        device.setVolume(0);
    }

}
