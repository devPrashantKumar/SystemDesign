package com.thecodeexperience.WithoutFlyweightDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class Forest {

    List<Tree> trees = new ArrayList<>();

    public void plantTree(String name, String color, int positionX, int positionY) {
        trees.add(new Tree(name, color, positionX, positionY));
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
