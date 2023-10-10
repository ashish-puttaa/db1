package org.example.types;

import org.example.types.attributes.IntegerAttribute;
import org.example.types.attributes.StringAttribute;

public class AttributeFactory {
    public static Attribute createFromBytes(byte[] bytes, Attribute.TYPES type) {
        if (type == Attribute.TYPES.STRING) {
            return StringAttribute.fromBytes(bytes);
        }
        else if (type == Attribute.TYPES.INTEGER) {
            return IntegerAttribute.fromBytes(bytes);
        }
        else {
            return null;
        }
    }
}
