package com.thecodeexperience.ProxyDesignPattern;

// SUBJECT — common interface for the real service and the caching proxy.
public interface DataService {
    String fetchData(String id);
}
