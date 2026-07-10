package com.thecodeexperience.AdapterDesignPattern;

import java.util.Map;

// TARGET INTERFACE — the contract the client expects.
// The client wants report data as JSON (represented here as a Map),
// regardless of what format the underlying provider actually produces.
public interface IReport {
    Map<String, String> getJSONReport();
}
