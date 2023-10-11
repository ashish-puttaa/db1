package org.example.types;

import org.example.types.attributes.Attribute;

import java.util.ArrayList;
import java.util.List;

public class PageHeaderColumn {
    public byte columnNumber;
    public Attribute.TYPES attributeType;
    public static final int SIZE = 2;

    public PageHeaderColumn(byte columnNumber, Attribute.TYPES attributeType) {
        this.columnNumber = columnNumber;
        this.attributeType = attributeType;
    }

    public byte[] serialize() {
        return new byte[] { columnNumber, attributeType.id };
    }

    public static List<PageHeaderColumn> fromAttributes(List<Attribute.TYPES> attributeTypes) {
        List<PageHeaderColumn> columnList = new ArrayList<>();

        for(int i=0; i<attributeTypes.size(); i++) {
            Attribute.TYPES type = attributeTypes.get(i);
            byte columnNumber = (byte) (i+1);
            columnList.add(new PageHeaderColumn(columnNumber, type));
        }

        return columnList;
    }
}
