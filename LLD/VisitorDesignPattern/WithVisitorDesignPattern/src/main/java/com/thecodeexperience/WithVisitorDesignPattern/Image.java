package com.thecodeexperience.WithVisitorDesignPattern;

/** CONCRETE ELEMENT. Data + accept(). Nothing else. */
public class Image implements DocumentElement {

    private final String url;
    private final String altText;

    public Image(String url, String altText) {
        this.url = url;
        this.altText = altText;
    }

    public String getUrl() {
        return url;
    }

    public String getAltText() {
        return altText;
    }

    @Override
    public <R> R accept(DocumentVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
