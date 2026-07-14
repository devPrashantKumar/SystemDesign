package com.thecodeexperience.WithoutInterpreterDesignPattern;

public class Main {

    public static void main(String[] args) {

        RuleEngine engine = new RuleEngine();
        NaiveRuleParser parser = new NaiveRuleParser();

        Metrics server = new Metrics().set("cpu", 60).set("memory", 10).set("disk", 10);
        System.out.println("server metrics: " + server);

        // --- ATTEMPT 1: rules hard-coded as Java ------------------------------------------
        System.out.println();
        System.out.println("=== hard-coded rules ===");
        System.out.println("  HIGH_CPU            → " + engine.evaluate("HIGH_CPU", server));
        System.out.println("  HIGH_CPU_AND_MEMORY → " + engine.evaluate("HIGH_CPU_AND_MEMORY", server));

        // ⚠ Ops wants a new rule. It does not exist, and it cannot exist until someone edits
        //   RuleEngine, gets it reviewed, and ships a release.
        try {
            engine.evaluate("CPU_SPIKE_OR_DISK_PRESSURE", server);
        } catch (IllegalArgumentException e) {
            System.out.println("  ⚠ " + e.getMessage());
        }

        // --- ATTEMPT 2: rules as strings, folded left to right ----------------------------
        System.out.println();
        System.out.println("=== BUG 1: no operator precedence ===");
        String rule1 = "cpu > 50 OR memory > 95 AND disk > 99";
        System.out.println("  rule    : " + rule1);
        System.out.println("  correct : cpu>50 OR (memory>95 AND disk>99)  →  true   (AND binds tighter)");
        System.out.println("  actual  : (cpu>50 OR memory>95) AND disk>99  →  " + parser.evaluate(rule1, server));
        System.out.println("  ⚠ The CPU is at 60%. This rule should be paging someone. It isn't.");

        System.out.println();
        System.out.println("=== BUG 2: no parentheses ===");
        Metrics diskFull = new Metrics().set("cpu", 10).set("memory", 10).set("disk", 99);
        String rule2 = "cpu > 80 AND ( memory > 90 OR disk > 95 )";
        System.out.println("  metrics : " + diskFull);
        System.out.println("  rule    : " + rule2);
        System.out.println("  correct : cpu>80 AND (...)  →  false   (the CPU is at 10%)");
        System.out.print("  actual  : ");
        boolean fired = parser.evaluate(rule2, diskFull);
        System.out.println("            (cpu>80 AND memory>90) OR disk>95  →  " + fired);
        System.out.println("  ⚠ A false page at 3am, because the parentheses were thrown away.");

        System.out.println();
        System.out.println("⚠ ROOT CAUSE: a language is a TREE. This evaluator is a LOOP.");
        System.out.println("  AND and OR aren't steps in a sequence — they're nodes with two children,");
        System.out.println("  and each child can be a whole expression. Fold a tree flat and the");
        System.out.println("  meaning is gone. No amount of patching the loop will fix that.");
    }

}
