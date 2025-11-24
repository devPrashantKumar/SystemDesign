package com.thecodeexperience.Practice01;

public class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee coffee){
        super(coffee);
    }

    @Override
    public int cost() {
        return coffee.cost()+5;
    }

    @Override
    public String description() {
        return coffee.description()+" - Sugar";
    }
}
