package org.example.entities.directory;

public class AttributeFactory {
    public static Attribute createFromBytes(byte[] bytes, AttributeType type) {
        if (type == AttributeType.STRING) {
            return StringAttribute.deserialize(bytes);
        }
        else if (type == AttributeType.INTEGER) {
            return IntegerAttribute.deserialize(bytes);
        }
        else {
            return null;
        }
    }
}
