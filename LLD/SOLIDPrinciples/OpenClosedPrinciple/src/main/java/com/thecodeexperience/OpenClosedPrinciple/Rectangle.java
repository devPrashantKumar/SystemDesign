package com.thecodeexperience.OpenClosedPrinciple;

public class Rectangle implements Shape{
    int height;
    int width;

    public Rectangle(int height, int width){
        this.height = height;
        this.width = width;
    }

    @Override
    public double area() {
        return this.height*this.width;
    }
}
