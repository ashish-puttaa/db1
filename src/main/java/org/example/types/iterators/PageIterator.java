package org.example.types.iterators;

import org.example.types.attributes.Attribute;
import org.example.types.Page;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public class PageIterator implements Iterator<Page> {
    List<Attribute.TYPES> attributeTypes;
    PageBytesIterator pageBytesIterator;

    public PageIterator(Path relationPath, int pageSize, List<Attribute.TYPES> attributeTypes) throws IOException {
        this.pageBytesIterator = new PageBytesIterator(relationPath, pageSize);
        this.attributeTypes = attributeTypes;
    }

    @Override
    public boolean hasNext() {
        return pageBytesIterator.hasNext();
    }

    @Override
    public Page next() {
        byte[] nextPageBytes = this.pageBytesIterator.next();
        return Page.fromBytes(nextPageBytes, this.attributeTypes);
    }
}
