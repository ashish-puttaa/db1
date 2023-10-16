package org.example.entities.relation;

public class AttributeFactory {
    public static Attribute<?> createFromBytes(byte[] bytes, AttributeType type) {
        if (type == AttributeType.CHAR) {
            return CharAttribute.deserialize(bytes);
        }
        else if (type == AttributeType.INTEGER) {
            return IntegerAttribute.deserialize(bytes);
        }
        else if(type == AttributeType.VARCHAR) {
            return VarcharAttribute.deserialize(bytes);
        }
        else {
            return null;
        }
    }
}
