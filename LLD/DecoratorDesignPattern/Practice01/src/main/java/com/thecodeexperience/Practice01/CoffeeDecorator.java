package com.thecodeexperience.Practice01;

public abstract class CoffeeDecorator implements Coffee {
    Coffee coffee;

    public CoffeeDecorator(Coffee coffee){
        this.coffee = coffee;
    }

    @Override
    public int cost() {
        return coffee.cost();
    }

    @Override
    public String description() {
        return coffee.description();
    }
}
