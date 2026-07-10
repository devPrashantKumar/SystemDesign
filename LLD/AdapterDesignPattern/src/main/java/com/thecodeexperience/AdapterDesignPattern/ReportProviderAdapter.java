package com.thecodeexperience.AdapterDesignPattern;

import java.util.Map;

// ADAPTER — bridges the incompatible interfaces.
// Implements the target interface (IReport) that the client speaks,
// and wraps the adaptee (XMLReportProvider) via composition — this is
// the "object adapter" form, preferred over inheriting the adaptee.
public class ReportProviderAdapter implements IReport {

    // Adaptee is held by composition and injected from outside,
    // so the adapter can wrap any XMLReportProvider instance.
    private final XMLReportProvider xmlReportProvider;

    public ReportProviderAdapter(XMLReportProvider xmlReportProvider) {
        this.xmlReportProvider = xmlReportProvider;
    }

    // Translation happens here: receive the call in the client's format,
    // forward it to the adaptee's incompatible API, convert the result back.
    @Override
    public Map<String, String> getJSONReport() {
        String data = xmlReportProvider.getXMLReport();   // call adaptee (XML)
        String username = data.replace("<username>", "").replace("</username>", "");
        return Map.of("username", username);              // return in client's format (JSON)
    }
}
