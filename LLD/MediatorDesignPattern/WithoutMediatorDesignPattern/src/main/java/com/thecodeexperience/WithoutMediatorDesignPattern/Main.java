package com.thecodeexperience.WithoutMediatorDesignPattern;

public class Main {

    public static void main(String[] args) {

        User alice = new User("Alice");
        User bob = new User("Bob");
        User carol = new User("Carol");
        User dave = new User("Dave");

        // ⚠ THE WIRING. Every pair must be connected, by hand, in both directions.
        //    4 users -> 12 addPeer calls. This is O(n^2) and it is all boilerplate.
        alice.addPeer(bob);   alice.addPeer(carol);  alice.addPeer(dave);
        bob.addPeer(alice);   bob.addPeer(carol);    bob.addPeer(dave);
        carol.addPeer(alice); carol.addPeer(bob);    carol.addPeer(dave);
        dave.addPeer(alice);  dave.addPeer(bob);     dave.addPeer(carol);

        System.out.println("--- Alice says hello ---");
        alice.send("Hello everyone");

        System.out.println();
        System.out.println("--- Dave mutes Bob, then Bob speaks ---");
        dave.mute("Bob");
        bob.send("Anyone there?");

        // Adding a 5th user means 8 more addPeer calls, touching all four
        // existing users. Removing one means finding it in everyone's peer list.
        // The wiring is the application's real structure, and it is invisible —
        // it lives in Main, not in any class.
    }

}
