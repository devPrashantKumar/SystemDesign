package com.thecodeexperience.WithIteratorDesignPattern;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * CONCRETE AGGREGATE — stores songs in an array.
 *
 * The array is now genuinely private. Nothing leaves this class except songs,
 * one at a time, through an iterator.
 */
public class ArrayPlaylist implements Playlist {

    private Song[] songs = new Song[4];
    private int count = 0;

    @Override
    public void add(Song song) {
        if (count == songs.length) {
            Song[] bigger = new Song[songs.length * 2];
            System.arraycopy(songs, 0, bigger, 0, count);
            songs = bigger;
        }
        songs[count++] = song;
    }

    @Override
    public Iterator<Song> iterator() {
        return new ArrayIterator();
    }

    /**
     * A SECOND traversal policy over the SAME data.
     *
     * This is the half of Iterator people forget: it is not only "hide the structure",
     * it is also "support more than one way to walk it, at the same time".
     */
    public Iterator<Song> reverseIterator() {
        return new ReverseArrayIterator();
    }

    /**
     * CONCRETE ITERATOR — the cursor lives HERE, not in the playlist.
     *
     * That is why two iterators over the same playlist do not interfere:
     * each one is a separate object with its own {@code index}.
     */
    private class ArrayIterator implements Iterator<Song> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public Song next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return songs[index++];
        }
    }

    private class ReverseArrayIterator implements Iterator<Song> {

        private int index = count - 1;

        @Override
        public boolean hasNext() {
            return index >= 0;
        }

        @Override
        public Song next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return songs[index--];
        }
    }

}
