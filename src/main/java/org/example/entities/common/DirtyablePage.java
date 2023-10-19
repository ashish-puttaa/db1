package org.example.entities.common;

public class DirtyablePage {
    private boolean dirty;

    protected DirtyablePage() {
        this.markAsClean();
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void markAsClean() {
        this.dirty = false;
    }

    protected void markAsDirty() {
        this.dirty = true;
    }
}
