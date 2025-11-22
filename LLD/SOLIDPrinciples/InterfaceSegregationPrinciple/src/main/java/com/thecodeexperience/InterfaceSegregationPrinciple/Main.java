package com.thecodeexperience.InterfaceSegregationPrinciple;

public class Main {
    public static void main(String[] args) {
        Robot robot = new Robot();
        Human human = new Human();
        robot.work();
        human.eat();
        human.work();
    }
}
