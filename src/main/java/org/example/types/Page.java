package org.example.types;

import org.example.Util;

import java.util.ArrayList;
import java.util.List;


public class Page {
    public List<Tuple> tupleList;

    public Page(List<Tuple> tupleList) {
        this.tupleList = tupleList;
    }

    public static Page fromBytes(byte[] pageBytes, List<Attribute.TYPES> attributeTypeList) {
        int tupleSize = attributeTypeList.stream().mapToInt(attributeType -> attributeType.size).sum();
        byte[][] tuples = Util.splitByteArray(pageBytes, tupleSize);

        List<Tuple> tupleList = new ArrayList<>();
        for(byte[] tuple: tuples) {
            tupleList.add(Tuple.fromBytes(tuple, attributeTypeList));
        }

        return new Page(tupleList);
    }
}
