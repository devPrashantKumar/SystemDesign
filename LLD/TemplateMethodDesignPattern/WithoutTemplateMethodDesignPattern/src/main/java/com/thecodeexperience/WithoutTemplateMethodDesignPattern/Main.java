package com.thecodeexperience.WithoutTemplateMethodDesignPattern;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== CSV import ===");
        CsvImporter csv = new CsvImporter();
        csv.importData("employees.csv");

        System.out.println();
        System.out.println("=== JSON import ===");
        JsonImporter json = new JsonImporter();
        json.importData("employees.json");

        // ⚠ THE DAMAGE. Two importers, one algorithm, written out by hand twice — and the
        //    second copy quietly lost three of the seven steps.
        System.out.println();
        System.out.println("--- what actually landed in the database ---");
        for (Employee employee : Database.rows()) {
            String flag = employee.salary() > 0 ? "" : "   ⚠ INVALID — should never have been saved";
            System.out.println("    " + employee + flag);
        }

        System.out.println();
        System.out.println("--- file handles ---");
        System.out.println("    csv  file still open? " + csv.isFileOpen());
        System.out.println("    json file still open? " + json.isFileOpen() + "   ⚠ LEAKED");

        System.out.println();
        System.out.println("⚠ JsonImporter never validated and never closed. It compiled, it ran,");
        System.out.println("  it printed nothing alarming. The algorithm was never written down —");
        System.out.println("  it was only ever a habit, copied by hand, and the copy drifted.");
        System.out.println();
        System.out.println("⚠ And now imagine the audit team asks for a checksum step. You get to");
        System.out.println("  add it to EVERY importer, and hope you don't miss one. Again.");
    }

}
