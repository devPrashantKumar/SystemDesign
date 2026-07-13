package com.thecodeexperience.WithVisitorDesignPattern;

/**
 * THE ELEMENT.
 *
 * Compare this with the "Without" version, which declared toHtml(), toMarkdown() and
 * wordCount() — and would have grown a method for every future feature.
 *
 * Here there is exactly ONE method, and it will never need a second, no matter how many
 * operations get written. The elements are closed for modification and the operations are
 * open for extension. That is the Open/Closed Principle, this time pointing the right way.
 */
public interface DocumentElement {

    /**
     * "Whatever you are, come in and act on me."
     *
     * This is half of DOUBLE DISPATCH — the mechanism the whole pattern rests on:
     *
     *   1st dispatch: element.accept(visitor) is a VIRTUAL call. Java resolves it at
     *                 runtime to Paragraph.accept / Image.accept / Table.accept. This is
     *                 where the element's real type is recovered — no instanceof needed.
     *
     *   2nd dispatch: inside that accept(), the element calls visitor.visit(this). Because
     *                 we are inside Paragraph, the compiler KNOWS `this` is a Paragraph, so
     *                 it statically picks the visit(Paragraph) overload. Which visitor object
     *                 runs is then decided at runtime, as usual.
     *
     * Two dispatches — one on the element's type, one on the visitor's type — and the right
     * (element, operation) pair meets. That pairing is the thing single dispatch cannot do,
     * and it is exactly what the instanceof ladder in the "Without" project was faking by hand.
     */
    <R> R accept(DocumentVisitor<R> visitor);

}
