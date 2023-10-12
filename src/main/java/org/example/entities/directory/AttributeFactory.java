package org.example.entities.directory;

public class AttributeFactory {
    public static Attribute createFromBytes(byte[] bytes, Attribute.TYPES type) {
        if (type == Attribute.TYPES.STRING) {
            return StringAttribute.deserialize(bytes);
        }
        else if (type == Attribute.TYPES.INTEGER) {
            return IntegerAttribute.deserialize(bytes);
        }
        else {
            return null;
        }
    }
}
