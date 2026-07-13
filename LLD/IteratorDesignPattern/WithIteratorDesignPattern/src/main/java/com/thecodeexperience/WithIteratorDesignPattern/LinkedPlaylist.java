package com.thecodeexperience.WithIteratorDesignPattern;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * CONCRETE AGGREGATE — stores songs in a chain of nodes.
 *
 * A completely different data structure from {@link ArrayPlaylist}, with a completely
 * different traversal — and yet, to a client, indistinguishable. Both are a Playlist
 * you can walk.
 *
 * {@code Node} is private now. It never escapes.
 */
public class LinkedPlaylist implements Playlist {

    private static class Node {
        private final Song song;
        private Node next;

        Node(Song song) {
            this.song = song;
        }
    }

    private Node head;
    private Node tail;

    @Override
    public void add(Song song) {
        Node node = new Node(song);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    @Override
    public Iterator<Song> iterator() {
        return new LinkedIterator();
    }

    /**
     * CONCRETE ITERATOR — walking a chain instead of indexing an array.
     *
     * Same interface. The client never learns the difference.
     */
    private class LinkedIterator implements Iterator<Song> {

        private Node current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Song next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Song song = current.song;
            current = current.next;
            return song;
        }
    }

}
