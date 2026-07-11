package com.thecodeexperience.ProxyDesignPattern;

// VIRTUAL PROXY — same interface as RealImage, but LAZY.
// Creating the proxy is cheap: it only stores the file name and does NOT
// load anything. The expensive RealImage is created on the FIRST display()
// call, and reused on every call after that.
//
// Benefit: if display() is never called, the disk load never happens.
public class ImageProxy implements Image {
    private final String fileName;
    private RealImage realImage;   // null until first use (lazy initialization)

    public ImageProxy(String fileName) {
        this.fileName = fileName;  // cheap — no disk access here
    }

    @Override
    public void display() {
        if (realImage == null) {                 // create the real object only once,
            realImage = new RealImage(fileName);  // on the first actual request
        }
        realImage.display();                      // then delegate
    }
}
