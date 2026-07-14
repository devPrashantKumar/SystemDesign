package com.thecodeexperience.WithTemplateMethodDesignPattern;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        CsvImporter csv = new CsvImporter();
        JsonImporter json = new JsonImporter();
        XmlImporter xml = new XmlImporter();

        // Every importer is a DataImporter, and there is exactly one entry point. The client
        // does not know or care which format it is driving.
        List<DataImporter> importers = List.of(csv, json, xml);
        List<String> paths = List.of("employees.csv", "employees.json", "employees.xml");

        for (int i = 0; i < importers.size(); i++) {
            System.out.println("=== " + paths.get(i) + " ===");
            importers.get(i).importData(paths.get(i));   // ← the template method. The only call.
            System.out.println();
        }

        // ✅ Every bad row was rejected — including the ones in JSON and XML, whose authors
        //    never wrote a line of validation code. They inherited it and could not opt out.
        System.out.println("--- what landed in the database ---");
        for (Employee employee : Database.rows()) {
            System.out.println("    " + employee);
        }
        System.out.println("    (no invalid rows — validate() is private to DataImporter and");
        System.out.println("     always runs, so no subclass can skip it)");

        // ✅ Every file was closed — close() is abstract, so it must exist, and the template
        //    method always calls it.
        System.out.println();
        System.out.println("--- file handles ---");
        System.out.println("    csv  still open? " + csv.isFileOpen());
        System.out.println("    json still open? " + json.isFileOpen());
        System.out.println("    xml  still open? " + xml.isFileOpen());

        System.out.println();
        System.out.println("--- what a subclass author CANNOT do anymore ---");
        System.out.println("    override importData()   → won't compile, it's final");
        System.out.println("    skip validate()         → can't, it's private to DataImporter");
        System.out.println("    forget close()          → can't, it's abstract AND always called");
        System.out.println("    save before validating  → can't, the order isn't theirs to write");
        System.out.println();
        System.out.println("✅ And that checksum step the audit team wanted? One method in");
        System.out.println("   DataImporter, one line in importData(). All three importers get it.");
        System.out.println("   In the 'Without' project that was three edits and a prayer.");
    }

}
