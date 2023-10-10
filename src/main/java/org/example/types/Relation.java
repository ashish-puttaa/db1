package org.example.types;

import org.example.types.attributes.Attribute;
import org.example.types.iterators.PageIterator;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


//TODO: Add number of pages to relation
public class Relation {
    public static final Logger LOGGER = Logger.getLogger(Relation.class.getName());

    Path path;
    int pageSize;
    List<Attribute.TYPES> attributeTypesList;

    public Relation(Path path, int pageSize, List<Attribute.TYPES> attributeTypesList) {
        this.path = path;
        this.pageSize = pageSize;
        this.attributeTypesList = attributeTypesList;
    }

    public Iterator<Page> getPageIterator() throws IOException {
        return new PageIterator(this.path, this.pageSize, this.attributeTypesList);
    }

    public Page readNthPage(int n) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(this.path.toFile(), "r")) {
            byte[] pageBytes = new byte[this.pageSize];

            int pageOffsetInRelation = this.pageSize * (n - 1);
            randomAccessFile.seek(pageOffsetInRelation);
            randomAccessFile.readFully(pageBytes);

            return Page.fromBytes(pageBytes, this.attributeTypesList);
        }
    }
}
