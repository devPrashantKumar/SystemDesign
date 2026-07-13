package com.thecodeexperience.WithVisitorDesignPattern;

/**
 * CONCRETE VISITOR — and note it returns a different type from the exporters.
 *
 * The visitor is free to produce whatever the operation produces: a String, a number, a
 * validation report, a rendered PDF. The elements never learn about any of it.
 */
public class WordCountVisitor implements DocumentVisitor<Integer> {

    @Override
    public Integer visit(Paragraph paragraph) {
        return paragraph.getText().trim().split("\\s+").length;
    }

    @Override
    public Integer visit(Image image) {
        return 0;
    }

    @Override
    public Integer visit(Table table) {
        int count = table.getHeaders().length;
        for (String[] row : table.getRows()) {
            count += row.length;
        }
        return count;
    }

    @Override
    public Integer visit(CodeBlock codeBlock) {
        return 0;
    }

}
