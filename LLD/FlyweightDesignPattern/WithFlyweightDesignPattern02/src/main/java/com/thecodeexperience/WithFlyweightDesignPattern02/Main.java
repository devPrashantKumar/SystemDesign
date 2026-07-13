package com.thecodeexperience.WithFlyweightDesignPattern02;

public class Main {

    static final int PARAGRAPH_REPEATS = 2000;

    static final String HEADING = "Design Patterns\n";
    static final String PARAGRAPH = "The flyweight pattern shares intrinsic state across many objects.\n";

    public static void main(String[] args) {
        Document document = new Document();

        long memoryBefore = usedMemory();

        document.write(HEADING, "Arial", 24, "Red");
        for (int i = 0; i < PARAGRAPH_REPEATS; i++) {
            document.write(PARAGRAPH, "Times", 12, "Black");
        }

        long memoryAfter = usedMemory();

        System.out.println("Characters typed     : " + document.characterCount());
        System.out.println("Glyph flyweights     : " + GlyphFactory.cachedGlyphCount());
        System.out.println("Bitmaps rasterized   : " + Glyph.rasterizeCount);
        System.out.println("Memory used          : " + ((memoryAfter - memoryBefore) / 1024) + " KB");

        System.out.println();
        System.out.println("--- first 6 characters of the document ---");
        document.renderFirst(6);
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }

}
