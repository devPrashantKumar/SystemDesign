package com.thecodeexperience.WithVisitorDesignPattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * THE OBJECT STRUCTURE — the fourth role, and the one most write-ups forget.
 *
 * Somebody has to walk the elements and hand each one to the visitor. That somebody is the
 * object structure. Here it is a flat list; in a compiler it would be an abstract syntax
 * tree, and in this repo's CompositeDesignPattern it would be the FileSystem tree.
 *
 * Note how cleanly Iterator and Visitor divide the work — they are partners, not rivals:
 *
 *     ITERATOR answers "what is the next element?"
 *     VISITOR  answers "what do I do with this element, given its type?"
 *
 * The for-each loop below is Iterator. The accept() call inside it is Visitor.
 */
public class Document implements Iterable<DocumentElement> {

    private final List<DocumentElement> elements = new ArrayList<>();

    public void add(DocumentElement element) {
        elements.add(element);
    }

    @Override
    public Iterator<DocumentElement> iterator() {
        return elements.iterator();
    }

    /**
     * Run one operation over the whole document.
     *
     * The traversal is written ONCE, here, and every visitor that will ever exist reuses it.
     * The client never writes a loop and never touches an element's type.
     */
    public <R> List<R> accept(DocumentVisitor<R> visitor) {
        List<R> results = new ArrayList<>();
        for (DocumentElement element : elements) {
            results.add(element.accept(visitor));
        }
        return results;
    }

}
