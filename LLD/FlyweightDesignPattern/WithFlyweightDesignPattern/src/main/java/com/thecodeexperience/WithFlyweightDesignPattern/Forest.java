package com.thecodeexperience.WithFlyweightDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class Forest {

    List<Tree> trees = new ArrayList<>();

    public void plantTree(String name, String color, int positionX, int positionY) {
        TreeType treeType = TreeTypeFactory.getTreeType(name, color);
        trees.add(new Tree(positionX, positionY, treeType));
    }

    public void draw() {
        for (Tree tree : trees) {
            tree.draw();
        }
    }

    public int treeCount() {
        return trees.size();
    }

}
