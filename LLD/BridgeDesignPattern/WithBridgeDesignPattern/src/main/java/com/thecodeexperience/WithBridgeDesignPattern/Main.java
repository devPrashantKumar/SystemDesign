package com.thecodeexperience.WithBridgeDesignPattern;

public class Main {

    public static void main(String[] args) {

        // the SAME basic remote class, driving two different devices
        System.out.println("--- basic remote + TV ---");
        RemoteControl basicRemote = new RemoteControl(new TV());
        basicRemote.togglePower();
        basicRemote.volumeUp();
        basicRemote.channelUp();
        basicRemote.togglePower();

        System.out.println();
        System.out.println("--- basic remote + Radio ---");
        RemoteControl sameClassDifferentDevice = new RemoteControl(new Radio());
        sameClassDifferentDevice.togglePower();
        sameClassDifferentDevice.volumeUp();
        sameClassDifferentDevice.togglePower();

        // the advanced remote is written ONCE and works with every device
        System.out.println();
        System.out.println("--- advanced remote + Radio ---");
        AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(new Radio());
        advancedRemote.togglePower();
        advancedRemote.volumeUp();
        advancedRemote.mute();
        advancedRemote.togglePower();

        // Speaker was added AFTER both remotes existed. Neither remote changed.
        System.out.println();
        System.out.println("--- advanced remote + Speaker (device added later) ---");
        AdvancedRemoteControl speakerRemote = new AdvancedRemoteControl(new Speaker());
        speakerRemote.togglePower();
        speakerRemote.volumeUp();
        speakerRemote.mute();
        speakerRemote.togglePower();

        // 2 remotes + 3 devices = 5 classes, not 6.
        // The next device costs 1 class. The next remote costs 1 class.
        // Growth is (remotes + devices), not (remotes x devices).
    }

}
