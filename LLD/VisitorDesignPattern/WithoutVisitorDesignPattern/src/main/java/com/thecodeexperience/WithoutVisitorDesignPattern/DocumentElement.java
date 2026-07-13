package com.thecodeexperience.WithoutVisitorDesignPattern;

/**
 * The element hierarchy of a document.
 *
 * ⚠ THE PROBLEM: every operation anyone will ever want to run on a document has to be
 *    declared HERE, and implemented in EVERY subclass below.
 *
 *    Today it is HTML export and Markdown export. Tomorrow the team wants a word count,
 *    then a plain-text export, then a spell-check, then a PDF renderer. Each one is a new
 *    method on this interface and a new method in Paragraph, Image AND Table.
 *
 *    The element classes are STABLE — a paragraph has been a paragraph for 40 years.
 *    The operations are what churn. But it is the stable classes we keep editing.
 *    That is the Open/Closed Principle failing exactly backwards.
 */
public interface DocumentElement {

    String toHtml();

    String toMarkdown();

    // Product wants a word count. Adding it here forces an edit in all three subclasses.
    int wordCount();
}
