package com.thecodeexperience.WithIteratorDesignPattern;

import java.util.Iterator;

/**
 * THE CLIENT — written ONCE, for every playlist that has ever existed or ever will.
 *
 * Compare with WithoutIteratorDesignPattern, where the client needed one method per
 * storage type. Here the client's code does not grow when a new playlist type is added.
 */
public class Player {

    /** Works on any Playlist, because a Playlist is just "something you can walk". */
    public void play(Playlist playlist) {
        for (Song song : playlist) {          // ← the for-each loop IS the Iterator pattern
            System.out.println("    ▶ " + song);
        }
    }

    /** Works on any traversal at all — forward, reverse, filtered, or a type not yet written. */
    public void play(Iterator<Song> songs) {
        while (songs.hasNext()) {
            System.out.println("    ▶ " + songs.next());
        }
    }

    public int totalSeconds(Playlist playlist) {
        int total = 0;
        for (Song song : playlist) {
            total += song.getSeconds();
        }
        return total;
    }

}
