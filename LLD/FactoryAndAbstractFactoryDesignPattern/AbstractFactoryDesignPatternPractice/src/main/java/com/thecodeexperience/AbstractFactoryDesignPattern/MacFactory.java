package com.thecodeexperience.AbstractFactoryDesignPattern;

public class MacFactory implements IUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

