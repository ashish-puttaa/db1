package org.example.entities.directory;

public interface Attribute<T> {
    T getValue();
    AttributeType getType();
    byte[] serialize();
}
