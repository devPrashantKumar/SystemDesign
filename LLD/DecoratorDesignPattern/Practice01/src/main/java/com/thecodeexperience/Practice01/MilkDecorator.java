package com.thecodeexperience.Practice01;

public class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee){
        super(coffee);
    }

    @Override
    public int cost() {
        return coffee.cost()+15;
    }

    @Override
    public String description() {
        return coffee.description()+" - Milk";
    }
}
