package org.example.entities.common;

public class Dirtyable {
    private boolean dirty;

    protected Dirtyable() {
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
