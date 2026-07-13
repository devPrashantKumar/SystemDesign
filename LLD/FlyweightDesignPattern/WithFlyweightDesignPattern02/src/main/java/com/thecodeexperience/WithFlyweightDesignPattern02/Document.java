package com.thecodeexperience.WithFlyweightDesignPattern02;

import java.util.ArrayList;
import java.util.List;

public class Document {

    List<TextCharacter> characters = new ArrayList<>();

    int currentRow = 0;
    int currentColumn = 0;

    public void write(String text, String fontFamily, int fontSize, String color) {
        for (char symbol : text.toCharArray()) {
            if (symbol == '\n') {
                currentRow++;
                currentColumn = 0;
                continue;
            }
            Glyph glyph = GlyphFactory.getGlyph(symbol, fontFamily, fontSize, color);
            characters.add(new TextCharacter(currentRow, currentColumn, glyph));
            currentColumn++;
        }
    }

    public void render() {
        for (TextCharacter character : characters) {
            character.draw();
        }
    }

    public void renderFirst(int count) {
        for (int i = 0; i < count && i < characters.size(); i++) {
            characters.get(i).draw();
        }
    }

    public int characterCount() {
        return characters.size();
    }

}
