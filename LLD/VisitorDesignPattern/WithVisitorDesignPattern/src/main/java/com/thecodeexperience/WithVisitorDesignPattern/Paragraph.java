package com.thecodeexperience.WithVisitorDesignPattern;

/**
 * CONCRETE ELEMENT.
 *
 * It knows what a paragraph is. It knows nothing about HTML, Markdown, plain text or
 * counting — and it never will, however many of those we add.
 */
public class Paragraph implements DocumentElement {

    private final String text;

    public Paragraph(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public <R> R accept(DocumentVisitor<R> visitor) {
        // `this` is statically known to be a Paragraph here, so the compiler binds the
        // visit(Paragraph) overload. That is the second half of the double dispatch.
        return visitor.visit(this);
    }

}
