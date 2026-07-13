package com.thecodeexperience.WithoutVisitorDesignPattern;

public class Table implements DocumentElement {

    private final String[] headers;
    private final String[][] rows;

    public Table(String[] headers, String[][] rows) {
        this.headers = headers;
        this.rows = rows;
    }

    @Override
    public String toHtml() {
        StringBuilder html = new StringBuilder("<table>");
        html.append("<tr>");
        for (String header : headers) {
            html.append("<th>").append(header).append("</th>");
        }
        html.append("</tr>");
        for (String[] row : rows) {
            html.append("<tr>");
            for (String cell : row) {
                html.append("<td>").append(cell).append("</td>");
            }
            html.append("</tr>");
        }
        return html.append("</table>").toString();
    }

    @Override
    public String toMarkdown() {
        StringBuilder md = new StringBuilder("| ");
        md.append(String.join(" | ", headers)).append(" |\n| ");
        md.append("--- | ".repeat(headers.length)).append("\n");
        for (String[] row : rows) {
            md.append("| ").append(String.join(" | ", row)).append(" |\n");
        }
        return md.toString().trim();
    }

    @Override
    public int wordCount() {
        int count = headers.length;
        for (String[] row : rows) {
            count += row.length;
        }
        return count;
    }

}
