package com.thecodeexperience.WithVisitorDesignPattern;

/** CONCRETE VISITOR — the entire Markdown feature, in one file. */
public class MarkdownExportVisitor implements DocumentVisitor<String> {

    @Override
    public String visit(Paragraph paragraph) {
        return paragraph.getText();
    }

    @Override
    public String visit(Image image) {
        return "![" + image.getAltText() + "](" + image.getUrl() + ")";
    }

    @Override
    public String visit(Table table) {
        StringBuilder md = new StringBuilder("| ");
        md.append(String.join(" | ", table.getHeaders())).append(" |\n| ");
        md.append("--- | ".repeat(table.getHeaders().length)).append("\n");
        for (String[] row : table.getRows()) {
            md.append("| ").append(String.join(" | ", row)).append(" |\n");
        }
        return md.toString().trim();
    }

    @Override
    public String visit(CodeBlock codeBlock) {
        return "```" + codeBlock.getLanguage() + "\n" + codeBlock.getCode() + "\n```";
    }

}
