package com.thecodeexperience.ProxyDesignPattern;

// REAL SUBJECT — the expensive object.
// The heavy work (loading the file from disk) happens in the CONSTRUCTOR,
// so simply creating a RealImage is costly. That cost is exactly what the
// virtual proxy wants to defer until it's actually needed.
public class RealImage implements Image {
    private final String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();   // expensive — runs as soon as the object is created
    }

    private void loadFromDisk() {
        System.out.println("Loading image from disk: " + fileName + " (expensive!)");
    }

    @Override
    public void display() {
        System.out.println("Displaying: " + fileName);
    }
}
