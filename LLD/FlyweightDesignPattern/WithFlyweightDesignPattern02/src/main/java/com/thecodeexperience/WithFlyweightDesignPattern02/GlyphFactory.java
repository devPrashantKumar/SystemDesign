package com.thecodeexperience.WithFlyweightDesignPattern02;

import java.util.HashMap;
import java.util.Map;

public class GlyphFactory {

    private static final Map<String, Glyph> glyphCache = new HashMap<>();

    private GlyphFactory() {
    }

    public static Glyph getGlyph(char symbol, String fontFamily, int fontSize, String color) {
        String key = symbol + "-" + fontFamily + "-" + fontSize + "-" + color;
        return glyphCache.computeIfAbsent(key, k -> new Glyph(symbol, fontFamily, fontSize, color));
    }

    public static int cachedGlyphCount() {
        return glyphCache.size();
    }

}
