package com.thecodeexperience.WithVisitorDesignPattern;

/**
 * CONCRETE VISITOR — the entire HTML feature, in one file.
 *
 * In the "Without" project this logic was smeared across four element classes. Here, all of
 * it sits together: if the HTML output is wrong, there is exactly one file to open. That is
 * the second, quieter benefit of Visitor — related logic that was scattered by type is now
 * gathered by OPERATION.
 */
public class HtmlExportVisitor implements DocumentVisitor<String> {

    @Override
    public String visit(Paragraph paragraph) {
        return "<p>" + paragraph.getText() + "</p>";
    }

    @Override
    public String visit(Image image) {
        return "<img src=\"" + image.getUrl() + "\" alt=\"" + image.getAltText() + "\"/>";
    }

    @Override
    public String visit(Table table) {
        StringBuilder html = new StringBuilder("<table><tr>");
        for (String header : table.getHeaders()) {
            html.append("<th>").append(header).append("</th>");
        }
        html.append("</tr>");
        for (String[] row : table.getRows()) {
            html.append("<tr>");
            for (String cell : row) {
                html.append("<td>").append(cell).append("</td>");
            }
            html.append("</tr>");
        }
        return html.append("</table>").toString();
    }

    @Override
    public String visit(CodeBlock codeBlock) {
        return "<pre><code class=\"" + codeBlock.getLanguage() + "\">"
                + codeBlock.getCode() + "</code></pre>";
    }

}
