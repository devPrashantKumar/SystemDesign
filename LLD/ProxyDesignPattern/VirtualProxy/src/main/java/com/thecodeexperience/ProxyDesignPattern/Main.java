package com.thecodeexperience.ProxyDesignPattern;

public class Main {
    public static void main(String[] args) {
        // Creating the proxy is cheap — nothing is loaded from disk yet.
        Image image = new ImageProxy("photo.png");
        System.out.println("Proxy created — notice NO disk load happened above.");

        System.out.println("\nFirst display() -> triggers the lazy load:");
        image.display();   // loads from disk, then displays

        System.out.println("\nSecond display() -> reuses the already-loaded image:");
        image.display();   // no reload — RealImage already exists
    }
}
