package com.thecodeexperience.WithoutFlyweightDesignPattern;

public class Tree {

    public static int textureLoadCount = 0;

    String name;
    String color;
    byte[] texture;

    int positionX;
    int positionY;

    Tree(String name, String color, int positionX, int positionY) {
        this.name = name;
        this.color = color;
        this.texture = loadTexture(name);
        this.positionX = positionX;
        this.positionY = positionY;
    }

    private byte[] loadTexture(String name) {
        textureLoadCount++;
        return new byte[1024];
    }

    public void draw() {
        System.out.println("Drawing " + color + " " + name + " at (" + positionX + ", " + positionY + ")");
    }

}
