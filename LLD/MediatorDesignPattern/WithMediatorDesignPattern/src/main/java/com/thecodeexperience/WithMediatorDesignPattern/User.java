package com.thecodeexperience.WithMediatorDesignPattern;

/**
 * COLLEAGUE — knows the mediator, and nothing else.
 *
 * Note what is NOT here: no peer list, no mute list, no idea how many other
 * users exist or whether anyone is listening. A colleague announces; it does
 * not route.
 */
public abstract class User {

    protected final ChatMediator mediator;
    protected final String name;

    protected User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public abstract void send(String message);

    public abstract void receive(String from, String message);

    public String getName() {
        return name;
    }

}
