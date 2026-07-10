package com.thecodeexperience.AdapterDesignPattern;

// CLIENT — depends only on the target interface (IReport).
// The report source is injected via the constructor, so the client
// has no idea XML exists anywhere; the adapter hides it completely.
// Swapping to a different provider requires zero changes here.
public class Client {
    IReport report;

    Client(IReport report){
        this.report = report;
    }

    void generateReport(){
        System.out.println(report.getJSONReport());
    }

}
