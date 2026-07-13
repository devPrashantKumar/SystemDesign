package com.thecodeexperience.WithVisitorDesignPattern;

/**
 * THE VISITOR.
 *
 * One method per concrete element type. A class that implements this interface IS an
 * operation over the whole document — export it, measure it, validate it, translate it.
 *
 * The type parameter R is what the operation produces: String for the exporters,
 * Integer for the word count. GoF's original had void here and made visitors accumulate
 * state; returning a value is the modern form and is usually cleaner.
 *
 * ✅ THE PAYOFF: a new operation is a NEW CLASS implementing this interface. Not one line
 *    of Paragraph, Image, Table or CodeBlock changes. PlainTextExportVisitor in this
 *    project was added exactly that way — go look at it, then check the element classes
 *    for a single edit made on its behalf. There are none.
 *
 * ⚠ THE PRICE, stated honestly: a new ELEMENT type is a new method here, and therefore an
 *    edit to every visitor that already exists. Visitor trades "easy to add elements" for
 *    "easy to add operations". Take that trade only when the element hierarchy is stable
 *    and the operations are the things that keep multiplying — which is the usual shape of
 *    an AST, a document model, or a compiler.
 */
public interface DocumentVisitor<R> {

    R visit(Paragraph paragraph);

    R visit(Image image);

    R visit(Table table);

    R visit(CodeBlock codeBlock);

}
