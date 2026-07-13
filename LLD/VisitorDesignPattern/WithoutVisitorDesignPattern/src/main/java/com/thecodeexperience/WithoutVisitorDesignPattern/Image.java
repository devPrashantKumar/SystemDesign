package com.thecodeexperience.WithoutVisitorDesignPattern;

public class Image implements DocumentElement {

    private final String url;
    private final String altText;

    public Image(String url, String altText) {
        this.url = url;
        this.altText = altText;
    }

    @Override
    public String toHtml() {
        return "<img src=\"" + url + "\" alt=\"" + altText + "\"/>";
    }

    @Override
    public String toMarkdown() {
        return "![" + altText + "](" + url + ")";
    }

    @Override
    public int wordCount() {
        return 0;   // an image has no words
    }

}
