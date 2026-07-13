package com.thecodeexperience.WithFlyweightDesignPattern;

import java.util.Random;

public class Main {

    static final int TREES_TO_PLANT = 100_000;

    static final String[] NAMES = {"Oak", "Pine", "Maple"};
    static final String[] COLORS = {"Green", "Dark Green", "Red"};

    public static void main(String[] args) {
        Random random = new Random(42);
        Forest forest = new Forest();

        long memoryBefore = usedMemory();

        for (int i = 0; i < TREES_TO_PLANT; i++) {
            int species = random.nextInt(NAMES.length);
            forest.plantTree(NAMES[species], COLORS[species], random.nextInt(1000), random.nextInt(1000));
        }

        long memoryAfter = usedMemory();

        System.out.println("Trees planted        : " + forest.treeCount());
        System.out.println("TreeType flyweights  : " + TreeTypeFactory.cachedTypeCount());
        System.out.println("Textures loaded      : " + TreeType.textureLoadCount);
        System.out.println("Memory used          : " + ((memoryAfter - memoryBefore) / (1024 * 1024)) + " MB");
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }

}
