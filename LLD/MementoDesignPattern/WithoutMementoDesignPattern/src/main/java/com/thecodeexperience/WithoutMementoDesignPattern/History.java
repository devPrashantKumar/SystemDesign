package com.thecodeexperience.WithoutMementoDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * The undo stack.
 *
 * ⚠ THE PROBLEM: this class has to take the editor APART to store it, so it now knows
 *    exactly how many fields an Editor has and what each one means. The editor's private
 *    state is spread across three lists that live in a completely different class.
 *
 * ⚠ AND THE BUG THIS DESIGN INVITES: `selectionEnd` was added to Editor later, and whoever
 *    added it updated the getters and setters but never came back here. save() does not
 *    store it and undo() does not restore it.
 *
 *    Nothing failed. Nothing warned. Undo just quietly restores TWO of the three fields and
 *    leaves the editor in a state it could never legally reach. Run Main and watch it.
 *
 *    This is not a careless-programmer problem — it is a DESIGN problem. Any design that
 *    makes you enumerate someone else's fields by hand will eventually miss one.
 */
public class History {

    private final List<String> contents = new ArrayList<>();
    private final List<Integer> cursors = new ArrayList<>();
    // private final List<Integer> selectionEnds = ...   ← never added. Nobody noticed.

    public void save(Editor editor) {
        contents.add(editor.getContent());
        cursors.add(editor.getCursor());
        // ⚠ editor.getSelectionEnd() is never saved.
    }

    public void undo(Editor editor) {
        if (contents.isEmpty()) {
            return;
        }
        int last = contents.size() - 1;
        editor.setContent(contents.remove(last));
        editor.setCursor(cursors.remove(last));
        // ⚠ ...and never restored. The editor is now internally inconsistent.
    }

}
