package com.thecodeexperience.WithoutVisitorDesignPattern;

/**
 * A paragraph of text.
 *
 * ⚠ Notice what is crammed into this one class: text data, HTML knowledge, Markdown
 *   knowledge, and counting logic. It has four reasons to change and only one of them
 *   is "what a paragraph is".
 */
public class Paragraph implements DocumentElement {

    private final String text;

    public Paragraph(String text) {
        this.text = text;
    }

    @Override
    public String toHtml() {
        return "<p>" + text + "</p>";
    }

    @Override
    public String toMarkdown() {
        return text;
    }

    @Override
    public int wordCount() {
        return text.trim().split("\\s+").length;
    }

}
