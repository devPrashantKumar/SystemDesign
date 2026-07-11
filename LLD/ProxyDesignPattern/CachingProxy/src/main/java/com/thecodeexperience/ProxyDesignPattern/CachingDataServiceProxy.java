package com.thecodeexperience.ProxyDesignPattern;

import java.util.HashMap;
import java.util.Map;

// CACHING PROXY — same interface as the real service, but remembers results.
// On a cache HIT it returns the stored value WITHOUT calling the real service;
// on a MISS it delegates once, stores the result, then returns it.
//
// The real service stays untouched — caching is a separate concern living here.
public class CachingDataServiceProxy implements DataService {
    private final DataService realDataService;          // the wrapped real subject
    private final Map<String, String> cache = new HashMap<>();

    public CachingDataServiceProxy(DataService realDataService) {
        this.realDataService = realDataService;
    }

    @Override
    public String fetchData(String id) {
        if (cache.containsKey(id)) {                     // cache HIT — skip the expensive call
            System.out.println("  (cache hit for id=" + id + ")");
            return cache.get(id);
        }
        String data = realDataService.fetchData(id);     // cache MISS — delegate once
        cache.put(id, data);                             // remember it for next time
        return data;
    }
}
