package com.thecodeexperience.WithMementoDesignPattern;

/**
 * THE ORIGINATOR — the object whose state we want to snapshot and roll back.
 *
 * Compare this class with the "Without" version. Every setter is gone. The three fields are
 * private and STAY private: nothing outside this class can move the cursor, blank the
 * content, or put the editor into a state it could not reach by typing.
 *
 * And yet undo works. That is the entire point of Memento:
 *
 *     ✅ you can save and restore an object's state WITHOUT opening the object up.
 *
 * The trick is that the object snapshots ITSELF. Nobody reaches in — the editor hands out a
 * sealed box (a Memento) and, later, accepts one back. It is the only class that can see
 * inside that box.
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

    /**
     * ✅ SAVE — the originator snapshots itself.
     *
     * Because the editor writes this method, it CANNOT forget a field the way the "Without"
     * project's History did: the state and the code that captures it live in the same class,
     * three lines apart. Add a `fontSize` field and you will be staring straight at this
     * constructor call when you do.
     */
    public Memento save(String label) {
        return new Memento(label, content, cursor, selectionEnd);
    }

    /**
     * ✅ RESTORE — and only the originator can open the box.
     *
     * memento.content is a private field of a nested class. Java lets the ENCLOSING class
     * read it, and nobody else. So this method compiles here and would not compile in
     * History, in Main, or anywhere else on earth.
     */
    public void restore(Memento memento) {
        this.content = memento.content;
        this.cursor = memento.cursor;
        this.selectionEnd = memento.selectionEnd;
    }

    @Override
    public String toString() {
        return "\"" + content + "\" [cursor=" + cursor + ", selectionEnd=" + selectionEnd + "]";
    }

    /**
     * THE MEMENTO — a sealed box. The state is in here, and it is immutable.
     *
     * This is GoF's "wide interface vs narrow interface", and Java's nested classes express
     * it exactly:
     *
     *   WIDE   (for the Editor):   full read access to content, cursor, selectionEnd.
     *                              The enclosing class can see a nested class's privates.
     *
     *   NARROW (for everyone else): getLabel(). That is the whole public surface.
     *                              History can hold this object, stack it, count it, print
     *                              its label — and cannot learn one thing about the editor's
     *                              internals, nor change them.
     *
     * Try adding `history.getMementos().get(0).content` in Main. It will not compile. The
     * encapsulation the "Without" project had to surrender is fully intact here.
     */
    public static final class Memento {

        private final String label;
        private final String content;
        private final int cursor;
        private final int selectionEnd;

        private Memento(String label, String content, int cursor, int selectionEnd) {
            this.label = label;
            this.content = content;
            this.cursor = cursor;
            this.selectionEnd = selectionEnd;
        }

        /** The NARROW interface — metadata only. Safe for the caretaker to use. */
        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return "Memento(" + label + ")";   // deliberately reveals nothing
        }

    }

}
