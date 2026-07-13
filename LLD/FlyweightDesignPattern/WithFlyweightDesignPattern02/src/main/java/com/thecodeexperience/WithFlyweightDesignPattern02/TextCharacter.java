package com.thecodeexperience.WithFlyweightDesignPattern02;

public class TextCharacter {

    int row;
    int column;

    Glyph glyph;

    TextCharacter(int row, int column, Glyph glyph) {
        this.row = row;
        this.column = column;
        this.glyph = glyph;
    }

    public void draw() {
        glyph.draw(row, column);
    }

}
