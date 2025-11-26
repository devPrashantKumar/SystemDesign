package com.thecodeexperience.AbstractFactoryDesignPattern;

public class Main {
    public static void main(String[] args) {
        IUIFactory uiFactory = UIFactory.getUIFactory("mac");
        Button button = uiFactory.createButton();
        Checkbox checkbox = uiFactory.createCheckbox();
        button.paint();
        checkbox.check();

        System.out.println("-----------------------------------------");
        IUIFactory uiFactory2 = UIFactory.getUIFactory("windows");
        Button button2 = uiFactory2.createButton();
        Checkbox checkbox2 = uiFactory2.createCheckbox();
        button2.paint();
        checkbox2.check();
    }
}

