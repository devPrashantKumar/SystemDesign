package com.thecodeexperience.WithFlyweightDesignPattern;

import java.util.HashMap;
import java.util.Map;

public class TreeTypeFactory {

    private static final Map<String, TreeType> treeTypeCache = new HashMap<>();

    private TreeTypeFactory() {
    }

    public static TreeType getTreeType(String name, String color) {
        String key = name + "-" + color;
        return treeTypeCache.computeIfAbsent(key, k -> new TreeType(name, color));
    }

    public static int cachedTypeCount() {
        return treeTypeCache.size();
    }

}
