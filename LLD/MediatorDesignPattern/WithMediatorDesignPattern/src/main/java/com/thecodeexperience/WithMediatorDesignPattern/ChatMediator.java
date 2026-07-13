package com.thecodeexperience.WithMediatorDesignPattern;

/**
 * MEDIATOR — the only thing a colleague is allowed to talk to.
 *
 * Colleagues no longer reference each other. They report events ("I sent a
 * message") to the mediator, and the mediator decides what happens next.
 */
public interface ChatMediator {

    void addUser(User user);

    void removeUser(User user);

    void sendMessage(String message, User sender);

    void sendPrivate(String message, User sender, String recipientName);

}
