package org.example.entities.relation;

import org.example.iterators.PageIterator;
import org.example.util.ByteUtil;

import java.io.IOException;
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

    public static Path getFilePath(int id) {
        String fileName = "relation-" + id;
        return Path.of(fileName);
    }

    public Page readNthPage(int n) throws IOException {
        int pageOffsetInRelation = this.pageSize * n;
        byte[] pageBytes = ByteUtil.readNBytes(this.path, this.pageSize, pageOffsetInRelation);
        return Page.deserialize(pageBytes);
    }
    public void writeNthPage(int n, Page page) throws IOException {
        int pageOffsetInRelation = this.pageSize * n;
        byte[] pageBytes = page.serialize();
        ByteUtil.writeNBytes(this.path, this.pageSize, pageOffsetInRelation, pageBytes);
    }
}
