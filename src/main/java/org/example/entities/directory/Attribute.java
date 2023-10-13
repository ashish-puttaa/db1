package org.example.entities.directory;

public interface Attribute<T> {
    T getValue();
    void setValue(T value);
    AttributeType getType();
    byte[] serialize();

    static Attribute<?> deserialize(byte[] bytes) {
        // Implementing classes must provide their own deserialization logic
        // This is just a placeholder, you may need to modify it based on your requirements
        throw new UnsupportedOperationException("Deserialization not implemented");
    }
}
