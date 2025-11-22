package com.thecodeexperience.WithoutInterfaceSegregationPrinciple;

public class Human implements Worker {
    public void work() {
        System.out.println("Human is working");
    }

    public void eat() {
        System.out.println("Human is eating");
    }
}
