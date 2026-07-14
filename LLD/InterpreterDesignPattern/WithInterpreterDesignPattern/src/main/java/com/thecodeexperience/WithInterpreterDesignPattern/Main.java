package com.thecodeexperience.WithInterpreterDesignPattern;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        RuleParser parser = new RuleParser();

        Context server = new Context().set("cpu", 60).set("memory", 10).set("disk", 10);
        System.out.println("server metrics: " + server);

        // --- 1. The tree, built BY HAND. No parser in sight. -------------------------------
        //
        // This is the pattern in its purest form: an expression is just objects wired together.
        // cpu > 50 OR (memory > 95 AND disk > 99)
        System.out.println();
        System.out.println("=== the tree, assembled by hand ===");
        Expression byHand = new OrExpression(
                new Comparison("cpu", Comparison.Operator.GREATER_THAN, 50),
                new AndExpression(
                        new Comparison("memory", Comparison.Operator.GREATER_THAN, 95),
                        new Comparison("disk", Comparison.Operator.GREATER_THAN, 99)));
        System.out.println("  " + byHand);
        System.out.println("  → " + byHand.interpret(server));

        // --- 2. The two bugs from the "Without" project, now structurally impossible --------
        System.out.println();
        System.out.println("=== FIXED 1: operator precedence ===");
        String rule1 = "cpu > 50 OR memory > 95 AND disk > 99";
        Expression parsed1 = parser.parse(rule1);
        System.out.println("  rule   : " + rule1);
        System.out.println("  tree   : " + parsed1 + "   ← AND is DEEPER, so it binds tighter");
        System.out.println("  result : " + parsed1.interpret(server) + "   ✅ (the 'Without' project said false)");

        System.out.println();
        System.out.println("=== FIXED 2: parentheses ===");
        Context diskFull = new Context().set("cpu", 10).set("memory", 10).set("disk", 99);
        String rule2 = "cpu > 80 AND ( memory > 90 OR disk > 95 )";
        Expression parsed2 = parser.parse(rule2);
        System.out.println("  metrics: " + diskFull);
        System.out.println("  rule   : " + rule2);
        System.out.println("  tree   : " + parsed2);
        System.out.println("  result : " + parsed2.interpret(diskFull) + "   ✅ (the 'Without' project paged at 3am)");

        // --- 3. Rules are DATA now. ---------------------------------------------------------
        //
        // Pretend this list came out of alerts.yaml. Ops wrote them this morning. Nobody
        // recompiled anything, and no rule below exists as a Java branch anywhere.
        System.out.println();
        System.out.println("=== rules loaded at runtime (imagine: alerts.yaml) ===");
        List<String> configuredRules = List.of(
                "cpu > 80",
                "cpu > 80 AND memory > 90",
                "disk > 95 OR memory > 90",
                "NOT cpu < 20",
                "( cpu > 50 OR memory > 50 ) AND NOT disk > 95");

        Context hotServer = new Context().set("cpu", 85).set("memory", 95).set("disk", 40);
        System.out.println("  metrics: " + hotServer);
        for (String rule : configuredRules) {
            Expression expression = parser.parse(rule);
            boolean fired = expression.interpret(hotServer);
            System.out.printf("    %-45s → %s%n", rule, fired ? "🔴 FIRE" : "  ok");
        }

        // --- 4. One tree, many contexts. ----------------------------------------------------
        //
        // The expression holds no state about any server, so parse once and evaluate against
        // the whole fleet.
        System.out.println();
        System.out.println("=== one parsed rule, evaluated across the fleet ===");
        Expression alert = parser.parse("cpu > 80 AND NOT disk > 95");
        System.out.println("  rule: " + alert);
        List<Context> fleet = List.of(
                new Context().set("cpu", 85).set("disk", 40),
                new Context().set("cpu", 85).set("disk", 99),
                new Context().set("cpu", 12).set("disk", 40));
        for (Context node : fleet) {
            System.out.printf("    %-32s → %s%n", node, alert.interpret(node) ? "🔴 FIRE" : "  ok");
        }

        System.out.println();
        System.out.println("✅ The grammar IS the class hierarchy. A sentence IS a tree of objects.");
        System.out.println("   Precedence and parentheses were never implemented — they're just");
        System.out.println("   the shape of the tree, and recursion does the rest.");
        System.out.println("✅ And the rules are DATA. Ops can write a new one without a deploy.");
    }

}
