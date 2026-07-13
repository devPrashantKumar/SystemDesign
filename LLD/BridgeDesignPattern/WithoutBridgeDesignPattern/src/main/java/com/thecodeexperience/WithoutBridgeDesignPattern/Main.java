package com.thecodeexperience.WithoutBridgeDesignPattern;

public class Main {

    public static void main(String[] args) {

        System.out.println("--- basic remote, TV ---");
        Remote basicTv = new BasicTVRemote();
        basicTv.turnOn();
        basicTv.volumeUp();
        basicTv.turnOff();

        System.out.println();
        System.out.println("--- advanced remote, Radio ---");
        AdvancedRadioRemote advancedRadio = new AdvancedRadioRemote();
        advancedRadio.turnOn();
        advancedRadio.volumeUp();
        advancedRadio.mute();
        advancedRadio.turnOff();

        // 2 remotes x 2 devices = 4 classes.
        // Add a Speaker  -> 6 classes.
        // Add a VoiceRemote -> 9 classes.
        // The hierarchy grows as (remotes x devices), and the device code is
        // duplicated once per remote type.
    }

}
