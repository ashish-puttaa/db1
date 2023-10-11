package org.example;

import org.example.types.PageHeader;
import org.example.types.attributes.Attribute;
import org.example.types.Page;
import org.example.types.Relation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Main {
    private static final Path RELATION_FILE_PATH = Path.of("relation-file-1");


    private static Page[] generatePages(int num) {
        Page[] pages = new Page[num];

        for(int i=0; i<num; i++) {
            pages[i] = Util.generateSamplePage(i, Constants.PAGE_SIZE);
        }

        return pages;
    }

    public static void main(String[] args) throws Exception {

        Files.write(RELATION_FILE_PATH, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for(Page page: generatePages(10)) {
            Files.writeString(RELATION_FILE_PATH, page.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
            );
        }

        Relation relation = new Relation(RELATION_FILE_PATH, Constants.PAGE_SIZE);
        Iterator<Page> pageIterator = relation.getPageIterator();

        while(pageIterator.hasNext()) {
            Page page = pageIterator.next();
            System.out.println("\n" + page + "\n");
        }

        List<Attribute.TYPES> types = new ArrayList<>();
        types.add(Attribute.TYPES.STRING);
        types.add(Attribute.TYPES.STRING);
        types.add(Attribute.TYPES.INTEGER);
        types.add(Attribute.TYPES.STRING);
        types.add(Attribute.TYPES.INTEGER);

        PageHeader pageHeader = PageHeader.fromAttributes(types);

        System.out.println(pageHeader.serialize());
        System.out.println(Arrays.toString(pageHeader.serialize()));

    }
}