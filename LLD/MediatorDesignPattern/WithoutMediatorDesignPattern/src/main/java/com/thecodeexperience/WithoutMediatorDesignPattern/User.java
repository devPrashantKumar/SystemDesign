package com.thecodeexperience.WithoutMediatorDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Every user holds a direct reference to every OTHER user.
 *
 * That is the problem. The objects are wired to each other in a mesh, so each
 * one has to know the whole population — and the rules of the conversation end
 * up smeared across all of them.
 */
public class User {

    private final String name;

    // ⚠ n-1 references, in every single user
    private final List<User> peers = new ArrayList<>();

    // ⚠ interaction POLICY, duplicated in every user
    private final List<String> muted = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    public void addPeer(User peer) {
        peers.add(peer);
    }

    public void mute(String userName) {
        muted.add(userName);
    }

    public void send(String message) {
        System.out.println(name + " sends: " + message);
        for (User peer : peers) {
            // ⚠ the sender enforces the receiver's mute list — the rule lives
            //    in the wrong object, and is re-implemented by every colleague
            if (!peer.muted.contains(name)) {
                peer.receive(name, message);
            }
        }
    }

    public void receive(String from, String message) {
        System.out.println("    " + name + " received from " + from + ": " + message);
    }

    public String getName() {
        return name;
    }

}
