package com.thecodeexperience.WithVisitorDesignPattern;

/** CONCRETE ELEMENT. Data + accept(). Nothing else. */
public class Table implements DocumentElement {

    private final String[] headers;
    private final String[][] rows;

    public Table(String[] headers, String[][] rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public String[] getHeaders() {
        return headers;
    }

    public String[][] getRows() {
        return rows;
    }

    @Override
    public <R> R accept(DocumentVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
