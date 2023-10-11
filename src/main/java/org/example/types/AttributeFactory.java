package org.example.types;

import org.example.Deserializer;
import org.example.types.attributes.Attribute;

public class AttributeFactory {
    public static Attribute createFromBytes(byte[] bytes, Attribute.TYPES type) {
        if (type == Attribute.TYPES.STRING) {
            return Deserializer.deserializeStringAttribute(bytes);
        }
        else if (type == Attribute.TYPES.INTEGER) {
            return Deserializer.deserializeIntegerAttribute(bytes);
        }
        else {
            return null;
        }
    }
}
