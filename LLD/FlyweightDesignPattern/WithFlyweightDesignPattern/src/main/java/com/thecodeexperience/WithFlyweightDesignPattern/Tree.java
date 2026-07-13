package com.thecodeexperience.WithFlyweightDesignPattern;

public class Tree {

    int positionX;
    int positionY;

    TreeType treeType;

    Tree(int positionX, int positionY, TreeType treeType) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.treeType = treeType;
    }

    public void draw() {
        treeType.draw(positionX, positionY);
    }

}
