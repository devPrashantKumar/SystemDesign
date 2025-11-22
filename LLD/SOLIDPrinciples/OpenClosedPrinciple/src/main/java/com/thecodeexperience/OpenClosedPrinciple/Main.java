package com.thecodeexperience.OpenClosedPrinciple;

public class Main {
    public static void main(String[] args) {
        AreaCalculator areaCalculator = new AreaCalculator();
        Circle circle = new Circle(5);
        Rectangle rectangle = new Rectangle(10,5);
        System.out.println("circle area : "+areaCalculator.calculateArea(circle));
        System.out.println("rectangle area : "+areaCalculator.calculateArea(rectangle));
    }
}
