package com.thecodeexperience.WithTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * THE ABSTRACT CLASS — it owns the algorithm. The subclasses only fill in the blanks.
 *
 * In the "Without" project the import algorithm existed nowhere. It was a habit that lived in
 * one class's method body and got copied by hand into the next. Here it is written down exactly
 * once, in importData(), and it is the only copy there will ever be.
 *
 * Read importData() and notice three KINDS of step, because that distinction is the whole
 * pattern:
 *
 *   ABSTRACT steps (open, readRaw, parse, close)
 *       genuinely differ per format. The subclass MUST supply them — it cannot compile
 *       otherwise. These are GoF's "primitive operations".
 *
 *   CONCRETE steps (validate, save, audit)
 *       are the same for everybody. They are `private`, which in Java means a subclass cannot
 *       override them, cannot skip them, and cannot even see them. JsonImporter's three missing
 *       steps are now UNSKIPPABLE — not by discipline, by the compiler.
 *
 *   HOOKS (transform, shouldDeduplicate)
 *       are optional steps with a sensible default. A subclass may override one; most won't.
 *       This is how the pattern stays flexible without letting anyone rewrite the skeleton.
 *
 * ✅ THE INVERSION: nobody calls the steps. The base class calls THEM. Subclasses no longer
 *    decide when to validate or whether to close — they are asked what "open" means for their
 *    format and nothing more. This is the Hollywood Principle: don't call us, we'll call you.
 */
public abstract class DataImporter {

    /**
     * ✅ THE TEMPLATE METHOD.
     *
     * The seven steps, in the one order that is correct, in one place. It is `final`, which is
     * not decoration — it is the enforcement. A subclass cannot override this method, so it
     * cannot reorder the steps, cannot save before it validates, and cannot forget to close.
     *
     * `final` here is doing the same job that `private` does on the concrete steps below: it
     * turns "please follow the algorithm" into "you have no way not to".
     */
    public final void importData(String path) {
        open(path);                                       // 1. abstract — per format
        List<String> raw = readRaw();                     // 2. abstract — per format
        List<Employee> parsed = parse(raw);               // 3. abstract — per format

        List<Employee> valid = validate(parsed);          // 4. private — ALWAYS runs
        List<Employee> records = transform(valid);        // 5. hook    — optional

        if (shouldDeduplicate()) {                        // 6. hook    — optional
            records = deduplicate(records);
        }

        save(records);                                    // 7. private — ALWAYS runs
        close();                                          // 8. abstract — per format
        audit(parsed.size(), records.size());             // 9. private — ALWAYS runs
    }

    // ---------------------------------------------------------------------------------------
    // PRIMITIVE OPERATIONS — abstract. The subclass has no choice but to supply these.
    // ---------------------------------------------------------------------------------------

    protected abstract void open(String path);

    protected abstract List<String> readRaw();

    protected abstract List<Employee> parse(List<String> raw);

    protected abstract void close();

    /** Just for the log prefix — keeps the output readable. */
    protected abstract String tag();

    // ---------------------------------------------------------------------------------------
    // HOOKS — optional steps. Default behaviour is "do nothing interesting"; override to opt in.
    // ---------------------------------------------------------------------------------------

    /**
     * HOOK: a chance to clean each record up. Default: leave it alone.
     * JsonImporter overrides this; the others don't need to.
     */
    protected List<Employee> transform(List<Employee> records) {
        return records;
    }

    /**
     * HOOK: does this source produce duplicate rows? Default: assume not.
     * CsvImporter overrides this to true — spreadsheet exports are full of duplicates.
     */
    protected boolean shouldDeduplicate() {
        return false;
    }

    // ---------------------------------------------------------------------------------------
    // INVARIANT STEPS — private, so no subclass can override, skip or even see them.
    //
    // This is where the "Without" project bled out. Every one of these steps was missing from
    // JsonImporter. Here, a subclass author could not omit them if they tried.
    // ---------------------------------------------------------------------------------------

    private List<Employee> validate(List<Employee> records) {
        List<Employee> valid = new ArrayList<>();
        for (Employee employee : records) {
            if (employee.salary() > 0) {
                valid.add(employee);
            } else {
                System.out.println("  " + tag() + " ✗ rejected invalid record: " + employee);
            }
        }
        return valid;
    }

    private List<Employee> deduplicate(List<Employee> records) {
        List<Employee> unique = new ArrayList<>(new LinkedHashSet<>(records));
        int removed = records.size() - unique.size();
        if (removed > 0) {
            System.out.println("  " + tag() + " removed " + removed + " duplicate record(s)");
        }
        return unique;
    }

    private void save(List<Employee> records) {
        for (Employee employee : records) {
            Database.insert(employee);
        }
    }

    private void audit(int read, int saved) {
        System.out.println("  " + tag() + " AUDIT: imported " + saved + " of " + read);
    }

}
