package com.thecodeexperiece.Practice02;

public class MilkDecorator implements Coffee {
    Coffee coffee;

    public MilkDecorator(Coffee coffee){
        this.coffee=coffee;
    }

    @Override
    public int cost() {
        return this.coffee.cost()+15;
    }

    @Override
    public String description() {
        return this.coffee.description()+" - Milk";
    }
}
