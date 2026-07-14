package com.thecodeexperience.WithInterpreterDesignPattern;

/**
 * A NONTERMINAL EXPRESSION with a single child — the grammar's unary operator.
 *
 * ✅ THE POINT OF THIS FILE: NOT was added to the language LAST. Adding it cost exactly this
 *    one class and one line in the parser. No existing expression class changed. Nothing that
 *    was already parsed or already running had to be touched.
 *
 *    That is what "the grammar is the class hierarchy" buys you: a new rule in the grammar is a
 *    new class, and the open/closed principle falls out for free.
 *
 * Grammar rule:  factor := "NOT" factor | ...
 */
public class NotExpression implements Expression {

    private final Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean interpret(Context context) {
        return !expression.interpret(context);
    }

    @Override
    public String toString() {
        return "NOT " + expression;
    }

}
