package org.example;

import org.example.types.*;
import org.example.types.attributes.Attribute;
import org.example.types.attributes.IntegerAttribute;
import org.example.types.attributes.StringAttribute;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deserializer {
    public static Page deserializePage(byte[] pageBytes) {
        int headerColumnCount = pageBytes[0];
        int headerEndOffset = PageHeader.getSerializedLength(headerColumnCount);

        byte[] pageHeaderBytes = Arrays.copyOfRange(pageBytes, 0, headerEndOffset);
        byte[] tuplesBytes = Arrays.copyOfRange(pageBytes, headerEndOffset, pageBytes.length);

        PageHeader pageHeader = deserializePageHeader(pageHeaderBytes);

        List<byte[]> tupleBytesList = Util.splitByteArray(tuplesBytes, pageHeader.getTupleLength());

        List<Tuple> tupleList = new ArrayList<>();
        for(int i=0; i<pageHeader.tupleCount; i++) {
            byte[] tupleBytes = tupleBytesList.get(i);
            tupleList.add(deserializeTuple(tupleBytes, pageHeader));
        }

        return new Page(pageHeader, tupleList);
    }

    public static PageHeader deserializePageHeader(byte[] bytes) {
        byte columnCount = bytes[0];
        List<PageHeaderColumn> columns = new ArrayList<>();

        for(int i=0; i<columnCount; i++) {
            int offset = 1 + i * PageHeaderColumn.SIZE; // 1 to remove columnCount byte
            byte[] pageHeaderBytes = Arrays.copyOfRange(bytes, offset, offset + PageHeaderColumn.SIZE);
            columns.add(deserializePageHeaderColumn(pageHeaderBytes));
        }

        int tupleCountOffsetStart = 1 + columnCount * PageHeaderColumn.SIZE;
        byte[] tupleCountBytes = Arrays.copyOfRange(bytes, tupleCountOffsetStart, tupleCountOffsetStart + 4);
        int tupleCount = ByteBuffer.wrap(tupleCountBytes).getInt();

        return new PageHeader(columns, tupleCount);
    }

    public static PageHeaderColumn deserializePageHeaderColumn(byte[] bytes) {
        Attribute.TYPES attributeType = Attribute.TYPES.getTypeById(bytes[1]);
        return new PageHeaderColumn(bytes[0], attributeType);
    }

    public static Tuple deserializeTuple(byte[] bytes, PageHeader pageHeader) {
        List<Attribute> attributeList = new ArrayList<>(pageHeader.columnList.size());

        int currentIndex = 0;

        for (PageHeaderColumn column : pageHeader.columnList) {
            Attribute.TYPES attributeType = column.attributeType;
            int toIndex = Math.min(currentIndex + attributeType.size, bytes.length);
            byte[] chunk = Arrays.copyOfRange(bytes, currentIndex, toIndex);

            Attribute attribute = AttributeFactory.createFromBytes(chunk, attributeType);
            attributeList.add(attribute);

            currentIndex = toIndex;
        }

        return new Tuple(attributeList);
    }

    public static IntegerAttribute deserializeIntegerAttribute(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new IntegerAttribute(buffer.getInt());
    }

    public static StringAttribute deserializeStringAttribute(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8).trim();
        return new StringAttribute(value);
    }
}
