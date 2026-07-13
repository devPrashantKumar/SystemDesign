package com.thecodeexperience.WithoutIteratorDesignPattern;

/**
 * The SAME idea — a list of songs — stored a completely different way: a chain of nodes.
 *
 * Same data. Same question ("give me the songs, in order"). Totally different traversal.
 */
public class LinkedPlaylist {

    // ⚠ THE PROBLEM: this internal type has to become public, because callers
    //    cannot walk the chain without it.
    public static class Node {
        public final Song song;
        public Node next;

        Node(Song song) {
            this.song = song;
        }
    }

    private Node head;
    private Node tail;

    public void add(Song song) {
        Node node = new Node(song);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
    }

    // ⚠ and here we hand out the chain itself
    public Node getHead() {
        return head;
    }

}
