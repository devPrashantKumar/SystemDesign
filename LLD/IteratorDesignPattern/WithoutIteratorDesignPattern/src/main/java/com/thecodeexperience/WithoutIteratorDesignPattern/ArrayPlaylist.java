package com.thecodeexperience.WithoutIteratorDesignPattern;

/**
 * A playlist backed by a plain array.
 *
 * To let anyone read it, it has to hand out its internals.
 */
public class ArrayPlaylist {

    private final Song[] songs = new Song[10];
    private int count = 0;

    public void add(Song song) {
        songs[count++] = song;
    }

    // ⚠ THE PROBLEM: the collection has to expose HOW it stores things.
    //    Callers now know it is an array, know it is over-allocated, and hold a
    //    reference they can write through: playlist.getSongs()[0] = null;
    public Song[] getSongs() {
        return songs;
    }

    public int getCount() {
        return count;
    }

}
