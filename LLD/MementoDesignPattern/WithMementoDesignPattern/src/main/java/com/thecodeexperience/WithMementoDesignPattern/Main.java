package com.thecodeexperience.WithMementoDesignPattern;

public class Main {

    public static void main(String[] args) {

        Editor editor = new Editor();
        History history = new History();

        history.save(editor, "empty document");
        editor.type("The quick brown fox");
        System.out.println("typed        : " + editor);

        history.save(editor, "after first sentence");
        editor.selectLastWord();
        editor.type(" jumps over the lazy dog");
        System.out.println("typed more   : " + editor);

        System.out.println();
        history.printTimeline();

        // ✅ ALL THREE FIELDS come back — including selectionEnd, the one the "Without"
        //    project silently dropped. The editor snapshotted itself, so it could not
        //    forget a field it owns.
        System.out.println();
        history.undo(editor);
        System.out.println("after undo   : " + editor);
        System.out.println("               ↑ content, cursor AND selectionEnd, all consistent.");

        // ✅ Redo comes free: the caretaker owns the history POLICY, and the snapshots are
        //    just values it can move between two stacks.
        System.out.println();
        history.redo(editor);
        System.out.println("after redo   : " + editor);

        System.out.println();
        history.undo(editor);
        history.undo(editor);
        System.out.println("back to start: " + editor);

        System.out.println();
        history.undo(editor);

        // ✅ AND THE THING THE "WITHOUT" PROJECT COULD NOT DO:
        //    there is no setContent(), no setCursor(), no setSelectionEnd(). The editor has
        //    no hole cut in it. Outside code cannot put it into an illegal state, because
        //    outside code cannot reach its state at all — not even History, which is
        //    literally holding its snapshots right now.
        System.out.println();
        System.out.println("--- what outside code CANNOT do anymore ---");
        System.out.println("    editor.setCursor(9999)        → does not exist");
        System.out.println("    editor.setContent(\"\")         → does not exist");
        System.out.println("    memento.content               → does not compile (private to Editor)");
        System.out.println("    Encapsulation intact, and undo still works.");
    }

}
