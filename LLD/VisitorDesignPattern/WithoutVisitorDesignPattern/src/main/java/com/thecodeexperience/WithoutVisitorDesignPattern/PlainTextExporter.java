package com.thecodeexperience.WithoutVisitorDesignPattern;

import java.util.List;

/**
 * THE OTHER BAD OPTION.
 *
 * Suppose you refuse to touch the element classes again — you keep the new operation
 * outside the hierarchy. The only way to do that without Visitor is to ask every object
 * what it is:
 *
 * ⚠ THE PROBLEM: an instanceof ladder.
 *    - The compiler cannot help you. Add a fourth element type (a CodeBlock, a Heading)
 *      and this ladder still compiles — it just silently falls through to the else and
 *      does the wrong thing at RUNTIME.
 *    - Every operation written this way needs its own ladder. Three operations, three
 *      ladders, all of which must be updated in lockstep whenever an element is added.
 *    - You are hand-rolling the type dispatch that the language already gives you for
 *      free through virtual method calls. Visitor's entire trick is getting that dispatch
 *      back (see the "With" project — it is called double dispatch).
 */
public class PlainTextExporter {

    public String export(List<DocumentElement> document) {
        StringBuilder text = new StringBuilder();

        for (DocumentElement element : document) {
            if (element instanceof Paragraph) {
                text.append(((Paragraph) element).toMarkdown()).append("\n");
            } else if (element instanceof Image) {
                text.append("[image]").append("\n");
            } else if (element instanceof Table) {
                text.append("[table]").append("\n");
            } else {
                // ⚠ a new element type lands here, silently, in production
                text.append("[unknown element — SILENTLY IGNORED]").append("\n");
            }
        }

        return text.toString().trim();
    }

}
