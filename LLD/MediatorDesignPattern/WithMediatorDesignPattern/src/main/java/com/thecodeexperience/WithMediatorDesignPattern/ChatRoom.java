package com.thecodeexperience.WithMediatorDesignPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CONCRETE MEDIATOR — the single place that knows how users interact.
 *
 * Every rule of the conversation lives here: who receives a broadcast, who is
 * muted, how a whisper is routed. Change the rules, change this one file.
 */
public class ChatRoom implements ChatMediator {

    private final List<User> users = new ArrayList<>();

    // interaction POLICY — stated once, in the object whose job it is
    private final Map<String, Set<String>> mutedBy = new HashMap<>();

    @Override
    public void addUser(User user) {
        users.add(user);
        System.out.println("* " + user.getName() + " joined the room");
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
        System.out.println("* " + user.getName() + " left the room");
    }

    /** {@code muter} will no longer receive messages from {@code mutedName}. */
    public void mute(User muter, String mutedName) {
        mutedBy.computeIfAbsent(muter.getName(), k -> new HashSet<>()).add(mutedName);
        System.out.println("* " + muter.getName() + " muted " + mutedName);
    }

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user == sender) {
                continue;                       // never echo back to the sender
            }
            if (isMuted(user, sender)) {
                continue;                       // the ONE place the mute rule is applied
            }
            user.receive(sender.getName(), message);
        }
    }

    @Override
    public void sendPrivate(String message, User sender, String recipientName) {
        for (User user : users) {
            if (user.getName().equals(recipientName) && !isMuted(user, sender)) {
                user.receive(sender.getName(), message);
                return;
            }
        }
        System.out.println("    (no such user: " + recipientName + ")");
    }

    private boolean isMuted(User receiver, User sender) {
        return mutedBy.getOrDefault(receiver.getName(), Set.of()).contains(sender.getName());
    }

}
