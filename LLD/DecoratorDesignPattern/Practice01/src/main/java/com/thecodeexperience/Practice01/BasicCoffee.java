package com.thecodeexperience.Practice01;

public class BasicCoffee implements Coffee {
    @Override
    public int cost() {
        return 10;
    }

    @Override
    public String description() {
        return "Basic Coffee";
    }
}
