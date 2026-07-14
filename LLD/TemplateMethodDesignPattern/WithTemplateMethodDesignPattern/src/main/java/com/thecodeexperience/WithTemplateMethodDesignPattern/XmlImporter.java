package com.thecodeexperience.WithTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * ✅ THE PAYOFF — a third format, added last.
 *
 * Look at what writing it cost: five short methods, all of them genuinely about XML. Not one
 * line about validation, saving, closing, auditing or the order of the steps. Those were
 * inherited, and could not have been got wrong even on purpose.
 *
 * In the "Without" project, adding this class meant copying seven steps by hand from a
 * neighbouring importer and hoping the neighbour you copied was one of the correct ones.
 */
public class XmlImporter extends DataImporter {

    private boolean fileOpen = false;

    @Override
    protected String tag() {
        return "[xml] ";
    }

    @Override
    protected void open(String path) {
        System.out.println("  [xml]  opening " + path);
        fileOpen = true;
    }

    @Override
    protected List<String> readRaw() {
        List<String> rawElements = new ArrayList<>();
        rawElements.add("<employee><name>Frank</name><salary>77000</salary></employee>");
        rawElements.add("<employee><name>Grace</name><salary>0</salary></employee>");   // invalid
        System.out.println("  [xml]  read " + rawElements.size() + " raw elements");
        return rawElements;
    }

    @Override
    protected List<Employee> parse(List<String> raw) {
        List<Employee> employees = new ArrayList<>();
        for (String element : raw) {
            String name = element.split("<name>")[1].split("</name>")[0];
            String salary = element.split("<salary>")[1].split("</salary>")[0];
            employees.add(new Employee(name, Integer.parseInt(salary)));
        }
        return employees;
    }

    @Override
    protected void close() {
        fileOpen = false;
        System.out.println("  [xml]  closed");
    }

    public boolean isFileOpen() {
        return fileOpen;
    }

}
