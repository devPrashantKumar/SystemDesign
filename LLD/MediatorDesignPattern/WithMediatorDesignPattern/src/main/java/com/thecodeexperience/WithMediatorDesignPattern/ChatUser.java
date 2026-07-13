package com.thecodeexperience.WithMediatorDesignPattern;

public class ChatUser extends User {

    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void send(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);   // "here is an event" — not "you, and you, and you"
    }

    public void whisper(String message, String recipientName) {
        System.out.println(name + " whispers to " + recipientName + ": " + message);
        mediator.sendPrivate(message, this, recipientName);
    }

    @Override
    public void receive(String from, String message) {
        System.out.println("    " + name + " received from " + from + ": " + message);
    }

}
