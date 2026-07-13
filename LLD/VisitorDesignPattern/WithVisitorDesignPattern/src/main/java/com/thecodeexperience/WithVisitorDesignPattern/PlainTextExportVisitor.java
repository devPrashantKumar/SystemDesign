package com.thecodeexperience.WithVisitorDesignPattern;

/**
 * ✅ THE POINT OF THE WHOLE PATTERN, DEMONSTRATED.
 *
 * This is a brand-new feature, added after the document model was already written and
 * shipped. Adding it required:
 *
 *    - zero edits to DocumentElement
 *    - zero edits to Paragraph, Image, Table or CodeBlock
 *    - zero edits to the other three visitors
 *    - one new file.
 *
 * Compare with the "Without" project, where the same feature meant either editing five
 * classes, or writing an instanceof ladder that silently dropped CodeBlock in production.
 *
 * And note what the compiler does for us here: this class cannot compile until it handles
 * EVERY element type. The silent-fall-through bug from the "Without" project is not merely
 * unlikely now — it is unrepresentable.
 */
public class PlainTextExportVisitor implements DocumentVisitor<String> {

    @Override
    public String visit(Paragraph paragraph) {
        return paragraph.getText();
    }

    @Override
    public String visit(Image image) {
        return "[image: " + image.getAltText() + "]";
    }

    @Override
    public String visit(Table table) {
        StringBuilder text = new StringBuilder("[table: ");
        text.append(String.join(", ", table.getHeaders()));
        return text.append(" — ").append(table.getRows().length).append(" rows]").toString();
    }

    @Override
    public String visit(CodeBlock codeBlock) {
        // The CodeBlock the instanceof ladder forgot. Here, forgetting it was not an option.
        return "[" + codeBlock.getLanguage() + " code: " + codeBlock.getCode() + "]";
    }

}
