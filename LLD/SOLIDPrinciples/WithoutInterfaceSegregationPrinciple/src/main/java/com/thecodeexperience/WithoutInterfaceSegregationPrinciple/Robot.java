package com.thecodeexperience.WithoutInterfaceSegregationPrinciple;

public class Robot implements Worker {
    public void work() {
        System.out.println("Robot is working");
    }

    public void eat() {
        System.out.println("Robot doesn't eat, unsupported behavior"); /* robot doesn't eat */
    }
}
