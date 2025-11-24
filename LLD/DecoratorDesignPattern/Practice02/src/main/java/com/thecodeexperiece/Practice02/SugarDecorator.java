package com.thecodeexperiece.Practice02;

public class SugarDecorator implements Coffee {
    Coffee coffee;

    public SugarDecorator(Coffee coffee){
        this.coffee=coffee;
    }

    @Override
    public int cost() {
        return this.coffee.cost()+5;
    }

    @Override
    public String description() {
        return this.coffee.description()+" - Sugar";
    }
}
