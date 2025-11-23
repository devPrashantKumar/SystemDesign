package com.thecodeexperience.WithoutLiskovSubstitutionPrinciple;

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = this.height = width; // forces both to be same
    }

    @Override
    public void setHeight(int height) {
        this.width = this.height = height;
    }
}
