package com.thecodeexperience.AdapterDesignPattern;

// COMPOSITION ROOT — the only place that knows the concrete wiring:
// adaptee → adapter → client. Everything else depends on interfaces.
public class Main {
    public static void main(String[] args) {
        IReport report = new ReportProviderAdapter(new XMLReportProvider());
        Client client = new Client(report);
        client.generateReport();
        // {username=Hello World}

        // If a CSVReportProvider arrives tomorrow, we only add a new
        // CSVReportProviderAdapter and change this wiring line —
        // Client and IReport never change.
    }
}
