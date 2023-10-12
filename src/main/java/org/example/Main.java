package org.example;

import org.example.entities.directory.Page;
import org.example.entities.directory.Relation;
import org.example.util.CommonUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Path RELATION_FILE_PATH = Path.of("relation-file-1");


    private static List<Page> generatePages(int num) {
        List<Page> pageList = new ArrayList<>();

        for(int i=0; i<num; i++) {
            Page page = CommonUtil.generateSamplePage(i+1);
            pageList.add(page);
        }

        return pageList;
    }

    public static void main(String[] args) throws Exception {

        Files.write(RELATION_FILE_PATH, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for(Page page: generatePages(10)) {
            Files.write(RELATION_FILE_PATH, page.serialize(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        }

        Relation relation = new Relation(RELATION_FILE_PATH, Constants.PAGE_SIZE);
        Iterator<Page> pageIterator = relation.getPageIterator();

        while(pageIterator.hasNext()) {
            Page page = pageIterator.next();

            System.out.printf("\nPAGE %d:\n", page.header.pageIdentifier);

            System.out.println("\nHEADER:");
            String headerString = page.header.columnList.stream().map(column -> column.columnNumber + "-" + column.attributeType).collect(Collectors.joining(", "));
            System.out.println(headerString);

            System.out.println("\nTUPLES:");
            page.tupleList.stream().forEach(tuple -> {
                String tupleString = tuple.attributeList.stream().map(attribute -> attribute.getValue().toString()).collect(Collectors.joining(":"));
                System.out.println(tupleString);
            });

            System.out.println("\n---------------------------------------------------------");
        }
    }
}