package com.thecodeexperience.WithFlyweightDesignPattern;

public class TreeType {

    public static int textureLoadCount = 0;

    private final String name;
    private final String color;
    private final byte[] texture;

    TreeType(String name, String color) {
        this.name = name;
        this.color = color;
        this.texture = loadTexture(name);
    }

    private byte[] loadTexture(String name) {
        textureLoadCount++;
        return new byte[1024];
    }

    public void draw(int positionX, int positionY) {
        System.out.println("Drawing " + color + " " + name + " at (" + positionX + ", " + positionY + ")");
    }

}
