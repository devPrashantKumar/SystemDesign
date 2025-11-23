package com.thecodeexperience.LiskovSubstitutionPrinciple;

public class Rectangle  implements Shape {
    private int width, height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    @Override
    public int area() {
        return this.height*this.width;
    }

}
