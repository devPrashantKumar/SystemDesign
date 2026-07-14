package com.thecodeexperience.WithInterpreterDesignPattern;

/**
 * A NONTERMINAL EXPRESSION — the same shape as AndExpression, with one operator changed.
 *
 * ✅ Where did operator precedence go? Look closely: it isn't here. There is no priority number,
 *    no precedence table, no comparison of ranks anywhere in this project.
 *
 *    That's because precedence is not a RULE that gets applied at evaluation time — it is
 *    already baked into the SHAPE OF THE TREE. "AND binds tighter than OR" simply means AND
 *    nodes end up DEEPER, so they are evaluated first, because that is what recursion does.
 *
 *    The parser decides the shape once. After that, precedence is a structural fact, and it is
 *    impossible to get wrong. Compare with the "Without" project, which had to get it right on
 *    every single evaluation — and didn't.
 *
 * Grammar rule:  expression := term ( "OR" term )*
 */
public class OrExpression implements Expression {

    private final Expression left;
    private final Expression right;

    public OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(Context context) {
        return left.interpret(context) || right.interpret(context);
    }

    @Override
    public String toString() {
        return "(" + left + " OR " + right + ")";
    }

}
