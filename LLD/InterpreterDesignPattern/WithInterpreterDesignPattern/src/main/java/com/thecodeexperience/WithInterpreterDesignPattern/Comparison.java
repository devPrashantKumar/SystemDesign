package com.thecodeexperience.WithInterpreterDesignPattern;

/**
 * A TERMINAL EXPRESSION — a leaf. It has no children, so interpret() does not recurse; it just
 * looks the metric up in the context and answers.
 *
 * Terminals are where the recursion bottoms out. Every expression tree, no matter how deep, is
 * ultimately a pile of these with operators wired between them.
 *
 * Grammar rule:  comparison := metric ( ">" | "<" ) number
 */
public class Comparison implements Expression {

    public enum Operator {
        GREATER_THAN(">"),
        LESS_THAN("<");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        public static Operator of(String symbol) {
            for (Operator operator : values()) {
                if (operator.symbol.equals(symbol)) {
                    return operator;
                }
            }
            throw new IllegalArgumentException("unknown comparison operator: " + symbol);
        }
    }

    private final String metric;
    private final Operator operator;
    private final double threshold;

    public Comparison(String metric, Operator operator, double threshold) {
        this.metric = metric;
        this.operator = operator;
        this.threshold = threshold;
    }

    @Override
    public boolean interpret(Context context) {
        double actual = context.get(metric);
        return switch (operator) {
            case GREATER_THAN -> actual > threshold;
            case LESS_THAN -> actual < threshold;
        };
    }

    @Override
    public String toString() {
        long rounded = Math.round(threshold);
        String number = (rounded == threshold) ? String.valueOf(rounded) : String.valueOf(threshold);
        return metric + " " + operator.symbol + " " + number;
    }

}
