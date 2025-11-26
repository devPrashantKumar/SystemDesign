package com.thecodeexperience.AbstractFactoryDesignPattern;

public class UIFactory {
    public static IUIFactory getUIFactory(String type) {
        if (type == null) return null;

        switch (type.toLowerCase()) {
            case "mac": return new MacFactory();
            case "windows": return new WindowsFactory();
            default: throw new IllegalArgumentException("Unknown shape " + type);
        }
    }
}
