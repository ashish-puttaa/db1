package org.example.types;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Relation {
    List<Page> pageList;
    Path path;
    List<Attribute.TYPES> attributeTypesList;

    public Relation(Path path, List<Page> pageList, List<Attribute.TYPES> attributeTypesList) {
        this.path = path;
        this.pageList = pageList;
        this.attributeTypesList = attributeTypesList;
    }

    public static Relation fromBytes(Path path, List<byte[]> pageBytesList, List<Attribute.TYPES> attributeTypesList) {
        List<Page> pageList = pageBytesList.stream().map(pageBytes -> Page.fromBytes(pageBytes, attributeTypesList)).collect(Collectors.toList());
        return new Relation(path, pageList, attributeTypesList);
    }
}
