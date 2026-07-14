package com.thecodeexperience.WithoutInterpreterDesignPattern;

/**
 * ATTEMPT 1 — hard-code every rule as a branch.
 *
 * This is what everyone writes first, and for three rules it is completely fine. The trouble is
 * what it costs to add the fourth.
 *
 * ⚠ Every new alert rule is:
 *
 *       a new `case` here  →  a code review  →  a build  →  a deploy
 *
 *   ...for what is, in the end, a slightly different arrangement of `>` and `&&`. The ops team
 *   knows exactly what they want to be alerted on; they just aren't allowed to say it. They have
 *   to file a ticket and wait for a release.
 *
 * The deeper problem: THE RULES ARE NOT DATA. They are Java. And that means they cannot be
 * stored in a config file, edited in a UI, tested by ops, or shipped without a deploy.
 */
public class RuleEngine {

    public boolean evaluate(String ruleName, Metrics metrics) {
        switch (ruleName) {
            case "HIGH_CPU":
                return metrics.get("cpu") > 80;

            case "HIGH_MEMORY":
                return metrics.get("memory") > 90;

            case "HIGH_CPU_AND_MEMORY":
                return metrics.get("cpu") > 80 && metrics.get("memory") > 90;

            case "DISK_ALMOST_FULL":
                return metrics.get("disk") > 95;

            // ...and so on, forever, one branch per combination anyone ever wants.

            default:
                throw new IllegalArgumentException(
                        "unknown rule: " + ruleName + "  ← file a ticket, wait for the next release");
        }
    }

}
