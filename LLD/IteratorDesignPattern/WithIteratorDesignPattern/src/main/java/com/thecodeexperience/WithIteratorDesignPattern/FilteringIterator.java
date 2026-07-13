package com.thecodeexperience.WithIteratorDesignPattern;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * An iterator that wraps another iterator and skips what you don't want.
 *
 * Notice what it is NOT coupled to: any playlist, any array, any node. It only
 * knows {@code Iterator}. So it works over the array playlist, the linked playlist,
 * and every playlist you write next year — including in reverse.
 *
 * That is the compounding payoff of the pattern: once traversal is an interface,
 * traversals become composable.
 */
public class FilteringIterator implements Iterator<Song> {

    private final Iterator<Song> source;
    private final Predicate<Song> keep;
    private Song nextMatch;

    public FilteringIterator(Iterator<Song> source, Predicate<Song> keep) {
        this.source = source;
        this.keep = keep;
        advance();
    }

    @Override
    public boolean hasNext() {
        return nextMatch != null;
    }

    @Override
    public Song next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Song current = nextMatch;
        advance();
        return current;
    }

    private void advance() {
        nextMatch = null;
        while (source.hasNext()) {
            Song candidate = source.next();
            if (keep.test(candidate)) {
                nextMatch = candidate;
                return;
            }
        }
    }

}
