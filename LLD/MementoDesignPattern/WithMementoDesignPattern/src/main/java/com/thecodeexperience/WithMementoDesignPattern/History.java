package com.thecodeexperience.WithMementoDesignPattern;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * THE CARETAKER — it holds the snapshots. It never looks inside one.
 *
 * Read this class and notice what is NOT here: no String content, no int cursor, no field of
 * the editor's anywhere. In the "Without" project the caretaker kept three parallel lists,
 * one per editor field, and had to be edited every time the editor grew. Here it holds
 * opaque boxes.
 *
 * ✅ THE CONSEQUENCE: add a `fontSize` to Editor and this class does not change. It cannot
 *    even tell that anything happened. The bug from the "Without" project — a caretaker
 *    silently forgetting to restore a field — is structurally impossible, because the
 *    caretaker never restores fields. It hands the whole box back.
 *
 * Its real job is the POLICY of history: what order, how deep, when to discard, undo vs
 * redo. That is genuinely its own responsibility, and it is now the only one it has.
 */
public class History {

    private final Deque<Editor.Memento> undoStack = new ArrayDeque<>();
    private final Deque<Editor.Memento> redoStack = new ArrayDeque<>();

    /** Snapshot the editor's current state before it changes. */
    public void save(Editor editor, String label) {
        undoStack.push(editor.save(label));
        redoStack.clear();          // a new edit invalidates the redo branch
    }

    public void undo(Editor editor) {
        if (undoStack.isEmpty()) {
            System.out.println("    (nothing to undo)");
            return;
        }
        redoStack.push(editor.save("before undo"));
        Editor.Memento memento = undoStack.pop();
        editor.restore(memento);    // ← hands the WHOLE box back. All-or-nothing.
        System.out.println("    undo → restored: " + memento.getLabel());
    }

    public void redo(Editor editor) {
        if (redoStack.isEmpty()) {
            System.out.println("    (nothing to redo)");
            return;
        }
        undoStack.push(editor.save("before redo"));
        Editor.Memento memento = redoStack.pop();
        editor.restore(memento);
        System.out.println("    redo → restored: " + memento.getLabel());
    }

    /**
     * The narrow interface is enough to be USEFUL. The caretaker can label, list and count
     * snapshots — it just cannot read or corrupt the editor's state.
     */
    public void printTimeline() {
        System.out.print("    undo stack (newest first): ");
        for (Editor.Memento memento : undoStack) {
            System.out.print("[" + memento.getLabel() + "] ");
        }
        System.out.println();

        // memento.content would NOT COMPILE here. That is the pattern working.
    }

}
