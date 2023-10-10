package org.example.types;

import org.example.types.attributes.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tuple {
    public final List<Attribute> attributeList;

    public Tuple(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public static Tuple fromBytes(byte[] bytes, List<Attribute.TYPES> attributeTypeList) {
        List<Attribute> attributeList = new ArrayList<>(attributeTypeList.size());

        int currentIndex = 0;

        for (Attribute.TYPES attributeType : attributeTypeList) {
            int toIndex = Math.min(currentIndex + attributeType.size, bytes.length);
            byte[] chunk = Arrays.copyOfRange(bytes, currentIndex, toIndex);

            Attribute attribute = AttributeFactory.createFromBytes(chunk, attributeType);
            attributeList.add(attribute);

            currentIndex = toIndex;
        }

        return new Tuple(attributeList);
    }

    @Override
    public String toString() {
        return attributeList.stream().map(Attribute::toString).collect(Collectors.joining());
    }
}
