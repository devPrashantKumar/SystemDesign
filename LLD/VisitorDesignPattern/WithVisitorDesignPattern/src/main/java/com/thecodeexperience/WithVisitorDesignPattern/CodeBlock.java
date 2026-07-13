package com.thecodeexperience.WithVisitorDesignPattern;

/** CONCRETE ELEMENT. Data + accept(). Nothing else. */
public class CodeBlock implements DocumentElement {

    private final String code;
    private final String language;

    public CodeBlock(String code, String language) {
        this.code = code;
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public <R> R accept(DocumentVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
