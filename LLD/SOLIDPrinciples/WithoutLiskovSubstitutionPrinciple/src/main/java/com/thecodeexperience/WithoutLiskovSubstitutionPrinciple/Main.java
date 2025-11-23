package com.thecodeexperience.WithoutLiskovSubstitutionPrinciple;

public class Main {
    public static void main(String[] args) {
        Rectangle r = new Rectangle();
        r.setWidth(10);
        r.setHeight(20);
        System.out.println(r.area()); // expects 200

        Rectangle r2 = new Square();
        r2.setWidth(10);
        r2.setHeight(20);
        System.out.println(r2.area()); // prints 400 (wrong!)

    }
}
