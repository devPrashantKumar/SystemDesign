package com.thecodeexperience.WithInterpreterDesignPattern;

/**
 * THE ABSTRACT EXPRESSION — one method, and it is the entire pattern.
 *
 * Interpreter's defining move is this:
 *
 *     ✅ ONE CLASS PER RULE OF THE GRAMMAR.
 *
 * Here is the grammar of our alert language:
 *
 *     expression := term ( "OR" term )*
 *     term       := factor ( "AND" factor )*
 *     factor     := "NOT" factor | "(" expression ")" | comparison
 *     comparison := metric ( ">" | "<" ) number
 *
 * And here is the class list:
 *
 *     OrExpression        ← "OR"
 *     AndExpression       ← "AND"
 *     NotExpression       ← "NOT"
 *     Comparison          ← metric > number      (a TERMINAL — it has no children)
 *
 * That is not a coincidence or a nice analogy. It is the pattern: **the grammar becomes the
 * class hierarchy, and a sentence in the language becomes a TREE OF OBJECTS.** Interpreting the
 * sentence is then just calling interpret() on the root and letting it recurse.
 *
 * This is why the "Without" project could not be saved by patching its loop. `AND` is not a step
 * — it is a node with two children. Once you model it as one, precedence and parentheses aren't
 * features you have to implement. They are just the SHAPE OF THE TREE.
 */
public interface Expression {

    /** Evaluate this node (and, if it has children, its whole subtree) against the context. */
    boolean interpret(Context context);

}
