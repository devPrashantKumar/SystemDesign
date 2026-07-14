package com.thecodeexperience.WithInterpreterDesignPattern;

/**
 * THE PARSER — and an honest note about it: **the parser is NOT part of the Interpreter pattern.**
 *
 * GoF are explicit about this. Interpreter defines how to REPRESENT and EVALUATE a sentence once
 * you have it as a tree; how you got the tree is your business. You could build the tree by hand
 * (Main does exactly that first), read it from JSON, or generate it with ANTLR.
 *
 * It's included here because a rule language nobody can type is not much of a rule language — and
 * because it shows the payoff clearly: this is a recursive-descent parser, and each method below
 * corresponds to one line of the grammar:
 *
 *     expression := term ( "OR" term )*          →  expression()
 *     term       := factor ( "AND" factor )*     →  term()
 *     factor     := "NOT" factor
 *                 | "(" expression ")"
 *                 | comparison                   →  factor()
 *     comparison := metric ( ">" | "<" ) number  →  comparison()
 *
 * ✅ PRECEDENCE IS THE NESTING. expression() calls term() calls factor(). Because AND is handled
 *    one level DEEPER than OR, AND nodes end up deeper in the tree, so they are evaluated first.
 *    Precedence isn't implemented — it is a consequence of the shape. That is the whole trick,
 *    and it is what the "Without" project's flat loop could never express.
 */
public class RuleParser {

    private String[] tokens;
    private int position;

    public Expression parse(String rule) {
        tokens = rule.replace("(", " ( ").replace(")", " ) ").trim().split("\\s+");
        position = 0;

        Expression expression = expression();

        if (position < tokens.length) {
            throw new IllegalArgumentException("unexpected token: " + tokens[position]);
        }
        return expression;
    }

    /** expression := term ( "OR" term )* */
    private Expression expression() {
        Expression left = term();
        while (match("OR")) {
            left = new OrExpression(left, term());
        }
        return left;
    }

    /** term := factor ( "AND" factor )*   ← one level deeper than OR, so AND binds tighter */
    private Expression term() {
        Expression left = factor();
        while (match("AND")) {
            left = new AndExpression(left, factor());
        }
        return left;
    }

    /** factor := "NOT" factor | "(" expression ")" | comparison */
    private Expression factor() {
        if (match("NOT")) {
            return new NotExpression(factor());
        }
        if (match("(")) {
            // ✅ Parentheses need no special handling — they just restart the grammar from the
            //    top, which nests a whole subtree right here. That is all a bracket ever was.
            Expression expression = expression();
            expect(")");
            return expression;
        }
        return comparison();
    }

    /** comparison := metric ( ">" | "<" ) number   ← the terminal */
    private Expression comparison() {
        String metric = next();
        Comparison.Operator operator = Comparison.Operator.of(next());
        double threshold = Double.parseDouble(next());
        return new Comparison(metric, operator, threshold);
    }

    // --- token plumbing ---------------------------------------------------------------------

    private boolean match(String token) {
        if (position < tokens.length && tokens[position].equals(token)) {
            position++;
            return true;
        }
        return false;
    }

    private String next() {
        if (position >= tokens.length) {
            throw new IllegalArgumentException("unexpected end of rule");
        }
        return tokens[position++];
    }

    private void expect(String token) {
        if (!match(token)) {
            throw new IllegalArgumentException("expected '" + token + "'");
        }
    }

}
