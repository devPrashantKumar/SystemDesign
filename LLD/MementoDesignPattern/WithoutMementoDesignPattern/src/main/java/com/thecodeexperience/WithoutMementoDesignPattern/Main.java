package com.thecodeexperience.WithoutMementoDesignPattern;

public class Main {

    public static void main(String[] args) {

        Editor editor = new Editor();
        History history = new History();

        editor.type("The quick brown fox");
        history.save(editor);
        System.out.println("typed        : " + editor);

        editor.selectLastWord();
        editor.type(" jumps over the lazy dog");
        System.out.println("typed more   : " + editor);

        // ⚠ BUG 1 — undo restores content and cursor, but NOT selectionEnd. History never
        //    knew that field existed. Nothing throws; the editor is just quietly wrong.
        history.undo(editor);
        System.out.println("after undo   : " + editor);
        System.out.println("               ↑ content is 19 chars, but selectionEnd is still 43.");
        System.out.println("                 A partial restore. Silent. This is the design's fault,");
        System.out.println("                 not a typo — nothing forced History to save all the state.");

        // ⚠ BUG 2 — the setters that exist ONLY for undo are public, so anyone can shove the
        //    editor into a state it could never reach by typing.
        System.out.println();
        editor.setCursor(9999);
        editor.setContent("");
        System.out.println("after random : " + editor);
        System.out.println("  outside code            ↑ empty document, cursor at 9999.");
        System.out.println("                            The editor cannot protect itself, because the");
        System.out.println("                            hole was cut in it on purpose, for undo.");
    }

}
