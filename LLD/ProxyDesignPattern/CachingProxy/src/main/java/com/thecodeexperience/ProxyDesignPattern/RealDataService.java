package com.thecodeexperience.ProxyDesignPattern;

// REAL SUBJECT — every call is expensive (imagine a DB query or remote API).
// It has no idea a cache exists; it always does the full work.
public class RealDataService implements DataService {
    @Override
    public String fetchData(String id) {
        System.out.println("  >> RealDataService: expensive fetch for id=" + id);
        return "Data(" + id + ")";
    }
}
