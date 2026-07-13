package com.thecodeexperience.WithoutVisitorDesignPattern;

/**
 * A fourth element type, added later.
 *
 * ⚠ Adding it cost us TWO separate kinds of pain:
 *
 *    1. It had to implement toHtml(), toMarkdown() AND wordCount() — every operation that
 *       has ever been bolted onto the hierarchy. If there were ten operations, this class
 *       would have ten methods before it was allowed to compile.
 *
 *    2. PlainTextExporter's instanceof ladder does NOT know about it, and the compiler
 *       said nothing. It compiles, ships, and quietly drops code blocks out of every
 *       plain-text export. Run Main and watch it happen.
 */
public class CodeBlock implements DocumentElement {

    private final String code;
    private final String language;

    public CodeBlock(String code, String language) {
        this.code = code;
        this.language = language;
    }

    @Override
    public String toHtml() {
        return "<pre><code class=\"" + language + "\">" + code + "</code></pre>";
    }

    @Override
    public String toMarkdown() {
        return "```" + language + "\n" + code + "\n```";
    }

    @Override
    public int wordCount() {
        return 0;   // code isn't prose
    }

}
