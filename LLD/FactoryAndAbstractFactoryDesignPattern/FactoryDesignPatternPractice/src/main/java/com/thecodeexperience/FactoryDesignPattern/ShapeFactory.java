package com.thecodeexperience.FactoryDesignPattern;

public class ShapeFactory {

    public static Shape getShape(String type) {
        if (type == null) return null;

        switch (type.toLowerCase()) {
            case "circle": return new Circle();
            case "rectangle": return new Rectangle();
            default: throw new IllegalArgumentException("Unknown shape " + type);
        }
    }
}
