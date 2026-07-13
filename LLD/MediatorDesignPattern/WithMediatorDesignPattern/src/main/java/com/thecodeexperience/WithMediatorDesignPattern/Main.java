package com.thecodeexperience.WithMediatorDesignPattern;

public class Main {

    public static void main(String[] args) {

        ChatRoom chatRoom = new ChatRoom();

        // ✅ THE WIRING. Each user connects to ONE thing — the room.
        //    No user knows that any other user exists. 4 users -> 4 lines.
        ChatUser alice = new ChatUser(chatRoom, "Alice");
        ChatUser bob = new ChatUser(chatRoom, "Bob");
        ChatUser carol = new ChatUser(chatRoom, "Carol");
        ChatUser dave = new ChatUser(chatRoom, "Dave");

        chatRoom.addUser(alice);
        chatRoom.addUser(bob);
        chatRoom.addUser(carol);
        chatRoom.addUser(dave);

        System.out.println();
        System.out.println("--- Alice says hello ---");
        alice.send("Hello everyone");

        System.out.println();
        System.out.println("--- Dave mutes Bob, then Bob speaks ---");
        chatRoom.mute(dave, "Bob");
        bob.send("Anyone there?");

        // A 5th user costs ONE line, and no existing user changes.
        System.out.println();
        System.out.println("--- Erin joins (one line, nobody else touched) ---");
        ChatUser erin = new ChatUser(chatRoom, "Erin");
        chatRoom.addUser(erin);
        erin.send("Hi, just got here");

        // New interaction rules land in the mediator, not in the colleagues.
        System.out.println();
        System.out.println("--- private message: a rule that lives only in the room ---");
        alice.whisper("Ignore Bob, he's in a mood", "Carol");

        System.out.println();
        System.out.println("--- Carol leaves, then Alice speaks ---");
        chatRoom.removeUser(carol);
        alice.send("Anyone want lunch?");
    }

}
