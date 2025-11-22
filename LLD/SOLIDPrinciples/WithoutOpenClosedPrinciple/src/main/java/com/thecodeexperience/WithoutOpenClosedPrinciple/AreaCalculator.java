package com.thecodeexperience.WithoutOpenClosedPrinciple;

public class AreaCalculator {

    public double calculateArea(Object shape) {
        if (shape instanceof Circle c) {
            return Math.PI * c.radius * c.radius;
        } else if (shape instanceof Rectangle r) {
            return r.width * r.height;
        }
        return 0;
    }
}

