package com.thecodeexperience.WithTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * A CONCRETE CLASS — it supplies the blanks, not the algorithm.
 *
 * Compare it with the "Without" version of the same class. The seven-step sequence is gone.
 * There is no order to get wrong here, because the order is not this class's business: this
 * class knows about commas, and nothing else.
 *
 * It also opts IN to one optional step — deduplication — via a hook. That is the pattern
 * flexing without anyone rewriting the skeleton.
 */
public class CsvImporter extends DataImporter {

    private boolean fileOpen = false;

    @Override
    protected String tag() {
        return "[csv] ";
    }

    @Override
    protected void open(String path) {
        System.out.println("  [csv]  opening " + path);
        fileOpen = true;
    }

    @Override
    protected List<String> readRaw() {
        List<String> rawLines = new ArrayList<>();
        rawLines.add("Alice,90000");
        rawLines.add("Bob,-5000");            // the same bad row as the "Without" project
        rawLines.add("Carol,120000");
        rawLines.add("Alice,90000");          // ...and a duplicate, because CSV exports do that
        System.out.println("  [csv]  read " + rawLines.size() + " raw lines");
        return rawLines;
    }

    @Override
    protected List<Employee> parse(List<String> raw) {
        List<Employee> employees = new ArrayList<>();
        for (String line : raw) {
            String[] parts = line.split(",");
            employees.add(new Employee(parts[0], Integer.parseInt(parts[1])));
        }
        return employees;
    }

    @Override
    protected void close() {
        fileOpen = false;
        System.out.println("  [csv]  closed");
    }

    /** ✅ HOOK — opting in to an optional step. The base class asked; this class answers. */
    @Override
    protected boolean shouldDeduplicate() {
        return true;
    }

    public boolean isFileOpen() {
        return fileOpen;
    }

}
