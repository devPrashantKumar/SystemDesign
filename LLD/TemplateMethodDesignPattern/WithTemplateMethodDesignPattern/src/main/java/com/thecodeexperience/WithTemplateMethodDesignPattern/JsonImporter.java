package com.thecodeexperience.WithTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that went wrong in the "Without" project — written again, under the template method.
 *
 * ✅ THE POINT OF THIS FILE: try to reintroduce the old bugs. You can't.
 *
 *     - skip validation?  You cannot. validate() is private to DataImporter. You cannot
 *                         override it, call it, or omit it. It runs.
 *     - forget to close?  You cannot. close() is abstract — this class does not compile
 *                         until it exists, and the template method always calls it.
 *     - skip the audit?   You cannot. Same reason as validation.
 *     - reorder the steps? You cannot. importData() is final.
 *
 * A careless author copying this class as a starting point for the next format can drop every
 * method in it and still not break the algorithm — because the algorithm was never in here.
 *
 * What IS in here: JSON, and one optional step this format happens to want.
 */
public class JsonImporter extends DataImporter {

    private boolean fileOpen = false;

    @Override
    protected String tag() {
        return "[json]";
    }

    @Override
    protected void open(String path) {
        System.out.println("  [json] opening " + path);
        fileOpen = true;
    }

    @Override
    protected List<String> readRaw() {
        List<String> rawObjects = new ArrayList<>();
        rawObjects.add("{\"name\":\"  dave \",\"salary\":85000}");    // note the sloppy whitespace
        rawObjects.add("{\"name\":\"Eve\",\"salary\":-1}");           // the row that got through before
        System.out.println("  [json] read " + rawObjects.size() + " raw objects");
        return rawObjects;
    }

    @Override
    protected List<Employee> parse(List<String> raw) {
        List<Employee> employees = new ArrayList<>();
        for (String object : raw) {
            String name = object.split("\"name\":\"")[1].split("\"")[0];
            String salary = object.split("\"salary\":")[1].replace("}", "");
            employees.add(new Employee(name, Integer.parseInt(salary)));
        }
        return employees;
    }

    @Override
    protected void close() {
        fileOpen = false;
        System.out.println("  [json] closed");
    }

    /**
     * ✅ HOOK — this feed ships names with stray whitespace and inconsistent case, so this
     * importer (and only this importer) cleans them up. The other two don't override it and
     * get the default no-op.
     */
    @Override
    protected List<Employee> transform(List<Employee> records) {
        List<Employee> cleaned = new ArrayList<>();
        for (Employee employee : records) {
            String name = employee.name().trim();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            cleaned.add(new Employee(name, employee.salary()));
        }
        System.out.println("  [json] transform hook: normalised " + cleaned.size() + " name(s)");
        return cleaned;
    }

    public boolean isFileOpen() {
        return fileOpen;
    }

}
