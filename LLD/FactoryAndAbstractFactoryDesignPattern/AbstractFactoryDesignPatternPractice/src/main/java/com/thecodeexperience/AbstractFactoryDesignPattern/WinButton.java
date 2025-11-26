package com.thecodeexperience.AbstractFactoryDesignPattern;

public class WinButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering Windows style button");
    }
}



