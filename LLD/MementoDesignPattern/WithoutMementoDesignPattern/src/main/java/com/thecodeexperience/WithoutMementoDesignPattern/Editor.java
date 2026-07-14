package com.thecodeexperience.WithoutMementoDesignPattern;

/**
 * A text editor that needs undo.
 *
 * Its state is three fields: the text, where the cursor is, and what is selected. All three
 * must be restored together — undo that puts the text back but leaves the cursor at
 * character 400 of a 12-character document is not undo, it is a bug.
 *
 * ⚠ THE PROBLEM: to let SOMEONE ELSE take a snapshot and put it back later, the editor has
 *    to open itself up completely — a getter AND a setter for every single field.
 *
 *    Look at what that costs:
 *
 *    1. Encapsulation is gone. setCursor() and setSelectionEnd() exist ONLY so that History
 *       can restore them. But they are public, so ANY code can now move the cursor to 9999
 *       and put the editor in a state it could never have reached through normal typing.
 *       The class can no longer defend its own invariants.
 *
 *    2. Every private field is now public API. Add a `fontSize` and you must add a getter,
 *       a setter, AND remember to update every place that snapshots or restores. Forget one
 *       and undo silently half-works — the worst kind of bug, because it looks fine.
 *
 *    3. The caretaker has to KNOW the editor's internals. See History: it stores three
 *       parallel lists, one per field, because it has to take the state apart to hold it.
 */
public class Editor {

    private String content = "";
    private int cursor = 0;
    private int selectionEnd = 0;

    public void type(String words) {
        content += words;
        cursor = content.length();
        selectionEnd = cursor;
    }

    public void selectLastWord() {
        int lastSpace = content.lastIndexOf(' ');
        cursor = lastSpace + 1;
        selectionEnd = content.length();
    }

    // ⚠ Every one of these exists only to make undo possible. They are not features.
    public String getContent()     { return content; }
    public int    getCursor()      { return cursor; }
    public int    getSelectionEnd(){ return selectionEnd; }

    public void setContent(String content)   { this.content = content; }
    public void setCursor(int cursor)        { this.cursor = cursor; }
    public void setSelectionEnd(int end)     { this.selectionEnd = end; }

    @Override
    public String toString() {
        return "\"" + content + "\" [cursor=" + cursor + ", selectionEnd=" + selectionEnd + "]";
    }

}
