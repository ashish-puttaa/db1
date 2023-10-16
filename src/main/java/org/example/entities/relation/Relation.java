package org.example.entities.relation;

import org.example.iterators.PageIterator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Logger;


//TODO: Add number of pages to relation
public class Relation {
    public static final Logger LOGGER = Logger.getLogger(Relation.class.getName());

    Path path;
    int pageSize;

    public Relation(Path path, int pageSize) {
        this.path = path;
        this.pageSize = pageSize;
    }

    public Iterator<Page> getPageIterator() throws IOException {
        return new PageIterator(this.path, this.pageSize);
    }

    public Page readNthPage(int n) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(this.path.toFile(), "r")) {
            byte[] pageBytes = new byte[this.pageSize];

            int pageOffsetInRelation = this.pageSize * (n - 1);
            randomAccessFile.seek(pageOffsetInRelation);
            randomAccessFile.readFully(pageBytes);

            return Page.deserialize(pageBytes);
        }
    }
}
