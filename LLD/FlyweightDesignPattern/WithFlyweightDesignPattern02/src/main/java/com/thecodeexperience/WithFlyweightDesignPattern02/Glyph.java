package com.thecodeexperience.WithFlyweightDesignPattern02;

public class Glyph {

    public static int rasterizeCount = 0;

    private final char symbol;
    private final String fontFamily;
    private final int fontSize;
    private final String color;

    private final byte[] rasterBitmap;

    Glyph(char symbol, String fontFamily, int fontSize, String color) {
        this.symbol = symbol;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.color = color;
        this.rasterBitmap = rasterize();
    }

    private byte[] rasterize() {
        rasterizeCount++;
        return new byte[512];
    }

    public void draw(int row, int column) {
        System.out.println("Drawing '" + symbol + "' at (row " + row + ", col " + column + ")"
                + " [" + fontFamily + " " + fontSize + "pt " + color + "]");
    }

}
