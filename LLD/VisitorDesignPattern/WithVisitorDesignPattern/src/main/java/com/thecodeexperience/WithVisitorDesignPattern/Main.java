package com.thecodeexperience.WithVisitorDesignPattern;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Document document = new Document();
        document.add(new Paragraph("The Visitor pattern separates an algorithm from the objects it runs on."));
        document.add(new Image("diagram.png", "Visitor structure"));
        document.add(new Table(
                new String[]{"Pattern", "Type"},
                new String[][]{{"Visitor", "Behavioural"}, {"Iterator", "Behavioural"}}));
        document.add(new CodeBlock("element.accept(visitor);", "java"));

        // ✅ Each operation is a whole feature, passed in as ONE object.
        //    The document is not asked what its elements are. Nothing says `instanceof`.
        System.out.println("--- HTML export ---");
        print(document.accept(new HtmlExportVisitor()));

        System.out.println();
        System.out.println("--- Markdown export ---");
        print(document.accept(new MarkdownExportVisitor()));

        // ✅ A visitor can return a different type entirely.
        System.out.println();
        List<Integer> counts = document.accept(new WordCountVisitor());
        int words = counts.stream().mapToInt(Integer::intValue).sum();
        System.out.println("--- Word count: " + words + " ---");
        System.out.println("    per element: " + counts);

        // ✅ THE PAYOFF. PlainTextExportVisitor was written LAST, after the element classes
        //    were finished. Adding it changed no element, no interface, and no other visitor.
        //    And unlike the instanceof ladder in the "Without" project, it CANNOT forget the
        //    CodeBlock — the compiler would have rejected the class.
        System.out.println();
        System.out.println("--- Plain-text export (a feature added with ZERO element edits) ---");
        print(document.accept(new PlainTextExportVisitor()));

        // ✅ A visitor written right here, inline, as an anonymous class. The document model
        //    is now open to operations that did not exist when it was compiled.
        System.out.println();
        System.out.println("--- Ad-hoc visitor: which elements are visual? ---");
        print(document.accept(new DocumentVisitor<String>() {
            @Override public String visit(Paragraph paragraph) { return "Paragraph  → text"; }
            @Override public String visit(Image image)         { return "Image      → VISUAL"; }
            @Override public String visit(Table table)         { return "Table      → VISUAL"; }
            @Override public String visit(CodeBlock codeBlock) { return "CodeBlock  → text"; }
        }));
    }

    private static void print(List<?> results) {
        for (Object result : results) {
            System.out.println("    " + result.toString().replace("\n", "\n    "));
        }
    }

}
