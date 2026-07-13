package com.thecodeexperience.WithIteratorDesignPattern;

import java.util.Iterator;

/**
 * AGGREGATE — a collection that can hand out an iterator over itself.
 *
 * GoF calls the method {@code createIterator()}. Java calls it {@code iterator()},
 * and spells the Aggregate interface {@link Iterable}. They are the same thing:
 * the Iterator pattern is baked into the language, and {@code for (Song s : playlist)}
 * is the pattern being used.
 *
 * Note what this interface does NOT expose: any hint of how the songs are stored.
 * No array. No nodes. No size. A caller cannot tell, and does not care.
 */
public interface Playlist extends Iterable<Song> {

    void add(Song song);

    /** The default traversal. This is GoF's {@code createIterator()}. */
    @Override
    Iterator<Song> iterator();

}
