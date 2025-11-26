package com.thecodeexperience.AbstractFactoryDesignPattern;

public class WindowsFactory implements IUIFactory {
    @Override
    public Button createButton() {
        return new WinButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WinCheckbox();
    }
}

