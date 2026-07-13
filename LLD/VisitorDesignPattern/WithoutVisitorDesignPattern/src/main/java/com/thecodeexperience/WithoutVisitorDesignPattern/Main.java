package com.thecodeexperience.WithoutVisitorDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<DocumentElement> document = new ArrayList<>();
        document.add(new Paragraph("The Visitor pattern separates an algorithm from the objects it runs on."));
        document.add(new Image("diagram.png", "Visitor structure"));
        document.add(new Table(
                new String[]{"Pattern", "Type"},
                new String[][]{{"Visitor", "Behavioural"}, {"Iterator", "Behavioural"}}));
        document.add(new CodeBlock("element.accept(visitor);", "java"));

        // Every operation below lives INSIDE the element classes. Each one was a new method
        // added to DocumentElement, Paragraph, Image, Table and CodeBlock — five edits per
        // feature, on classes that had no business changing.
        System.out.println("--- HTML export ---");
        for (DocumentElement element : document) {
            System.out.println("    " + element.toHtml());
        }

        System.out.println();
        System.out.println("--- Markdown export ---");
        for (DocumentElement element : document) {
            System.out.println("    " + element.toMarkdown().replace("\n", "\n    "));
        }

        System.out.println();
        int words = 0;
        for (DocumentElement element : document) {
            words += element.wordCount();
        }
        System.out.println("--- Word count: " + words + " ---");

        // ⚠ And here is the OTHER way to fail: keep the operation outside the hierarchy
        //    and dispatch on the type by hand.
        System.out.println();
        System.out.println("--- Plain-text export (via instanceof ladder) ---");
        System.out.println(indent(new PlainTextExporter().export(document)));
        System.out.println();
        System.out.println("    ⚠ The CodeBlock vanished. PlainTextExporter has no branch for it,");
        System.out.println("      and the compiler never warned us. It fell into the else at runtime.");
    }

    private static String indent(String text) {
        return "    " + text.replace("\n", "\n    ");
    }

}
