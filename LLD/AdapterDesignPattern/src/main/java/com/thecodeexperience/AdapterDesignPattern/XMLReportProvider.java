package com.thecodeexperience.AdapterDesignPattern;

// ADAPTEE — the existing/legacy class with an incompatible interface.
// Imagine this is third-party or legacy code we cannot modify:
// it only knows how to produce XML, and the client wants JSON.
public class XMLReportProvider {

    public String getXMLReport() {
        return "<username>Hello World</username>";
    }

}
