package com.thecodeexperience.WithoutInterpreterDesignPattern;

/**
 * ATTEMPT 2 — "fine, let ops write the rules as strings, and we'll interpret them."
 *
 * The instinct is right! The rules SHOULD be data. But look at how this gets built the first
 * time: one flat method that walks the tokens left to right and folds the result as it goes.
 *
 * ⚠ It works on every rule anybody tries at first — and then it is quietly, catastrophically
 *   wrong, in two different ways:
 *
 *   1. NO OPERATOR PRECEDENCE. It folds strictly left to right, so
 *
 *          cpu > 50 OR memory > 95 AND disk > 99
 *
 *      is evaluated as ((cpu>50 OR memory>95) AND disk>99), when AND binds tighter than OR and
 *      it should be (cpu>50 OR (memory>95 AND disk>99)). A CPU spike no longer pages anyone.
 *
 *   2. NO PARENTHESES. There is nowhere to put them, because a flat left-to-right fold has no
 *      notion of a subexpression. It strips them and carries on.
 *
 * THE ROOT CAUSE, and it is worth naming precisely: **a language is a TREE, and this code is a
 * LOOP.** `AND` and `OR` are not steps in a sequence — they are nodes with two children, and
 * those children can themselves be whole expressions. You cannot fold a tree flat and expect it
 * to keep its meaning. Every "quick expression evaluator" ever written dies on exactly this.
 */
public class NaiveRuleParser {

    public boolean evaluate(String rule, Metrics metrics) {
        if (rule.contains("(")) {
            System.out.println("      ⚠ parentheses are not supported — stripping them and hoping");
            rule = rule.replace("(", "").replace(")", "");
        }

        String[] tokens = rule.trim().split("\\s+");

        // the first comparison: <metric> <op> <threshold>
        boolean result = compare(tokens[0], tokens[1], tokens[2], metrics);

        // ...then fold everything else in, strictly left to right. ⚠ THIS is the bug.
        int i = 3;
        while (i < tokens.length) {
            String operator = tokens[i];
            boolean next = compare(tokens[i + 1], tokens[i + 2], tokens[i + 3], metrics);

            if (operator.equals("AND")) {
                result = result && next;
            } else if (operator.equals("OR")) {
                result = result || next;
            } else {
                throw new IllegalArgumentException("unknown operator: " + operator);
            }
            i += 4;
        }
        return result;
    }

    private boolean compare(String metric, String operator, String threshold, Metrics metrics) {
        double actual = metrics.get(metric);
        double limit = Double.parseDouble(threshold);
        return switch (operator) {
            case ">" -> actual > limit;
            case "<" -> actual < limit;
            default -> throw new IllegalArgumentException("unknown comparison: " + operator);
        };
    }

}
