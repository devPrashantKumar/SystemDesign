package com.thecodeexperience.WithoutLiskovSubstitutionPrinciple;

public class Rectangle {
    protected int width, height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int area() { return this.height*this.width; }

}
