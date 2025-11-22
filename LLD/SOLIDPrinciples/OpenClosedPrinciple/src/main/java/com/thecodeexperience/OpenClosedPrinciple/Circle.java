package com.thecodeexperience.OpenClosedPrinciple;

public class Circle implements Shape {
    int radius;

    public Circle(int radius){
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * this.radius * this.radius;
    }
}
