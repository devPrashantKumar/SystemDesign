package com.thecodeexperience.WithInterpreterDesignPattern;

/**
 * A NONTERMINAL EXPRESSION — it holds two children, and each child is itself an Expression.
 *
 * ✅ THIS is the line the "Without" project could never write:
 *
 *        private final Expression left;
 *        private final Expression right;
 *
 *    Not a boolean. Not a comparison. An EXPRESSION — which might be a leaf, or might be an
 *    entire nested rule fifty nodes deep. That recursion is what makes the thing a language
 *    instead of a list.
 *
 * And notice that interpret() does not know or care which. It asks its children and combines the
 * answers. (Yes — this is Composite, applied to a grammar. See CompositeDesignPattern/.)
 *
 * Grammar rule:  term := factor ( "AND" factor )*
 */
public class AndExpression implements Expression {

    private final Expression left;
    private final Expression right;

    public AndExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(Context context) {
        return left.interpret(context) && right.interpret(context);
    }

    @Override
    public String toString() {
        return "(" + left + " AND " + right + ")";
    }

}
