package com.thecodeexperience.LiskovSubstitutionPrinciple;

public class Square implements Shape {
    private int side;

    public void setSide(int side) {
        this.side = side;
    }

    @Override
    public int area() {
        return this.side*this.side;
    }
}
