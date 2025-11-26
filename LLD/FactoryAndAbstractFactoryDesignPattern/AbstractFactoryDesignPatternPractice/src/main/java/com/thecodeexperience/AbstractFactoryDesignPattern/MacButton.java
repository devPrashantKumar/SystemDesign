package com.thecodeexperience.AbstractFactoryDesignPattern;

public class MacButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering Mac style button");
    }
}


