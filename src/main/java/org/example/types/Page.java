package org.example.types;

import org.example.Constants;
import org.example.Util;
import org.example.types.attributes.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


//TODO: Make page number the default header
public class Page {
    public List<Tuple> tupleList;
    public Integer pageNumber;


    public Page(List<Tuple> tupleList) {
        this.tupleList = tupleList;
    }

    public Page(List<Tuple> tupleList, int pageNumber) {
        this.tupleList = tupleList;
        this.pageNumber = pageNumber;
    }

    //TODO: Remove page header and footer before converting them into tuples
    public static Page fromBytes(byte[] pageBytes, List<Attribute.TYPES> attributeTypeList) {
        int tupleSize = attributeTypeList.stream().mapToInt(attributeType -> attributeType.size).sum();
        List<byte[]> tupleBytesList = Util.splitByteArray(pageBytes, tupleSize);

        List<Tuple> tupleList = new ArrayList<>();
        for(byte[] tupleBytes: tupleBytesList) {
            tupleList.add(Tuple.fromBytes(tupleBytes, attributeTypeList));
        }

        return new Page(tupleList);
    }

    @Override
    public String toString() {
        String tupleString = this.tupleList.stream().map(Tuple::toString).collect(Collectors.joining());

        if(this.pageNumber != null) {
            String pageHeader = String.format(Constants.PAGE_HEADER, this.pageNumber);
            String pageFooter = String.format(Constants.PAGE_FOOTER, this.pageNumber);
            return pageHeader + tupleString + pageFooter;
        }
        else {
            return tupleString;
        }
    }
}
