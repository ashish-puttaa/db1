package org.example;

import org.example.entities.directory.Page;
import org.example.entities.directory.Relation;
import org.example.entities.directory.Tuple;
import org.example.util.MockPageFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Path RELATION_FILE_PATH = Path.of("relation-file-1");

    public static void main(String[] args) throws Exception {

        Files.write(RELATION_FILE_PATH, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for(int i=0; i<10; i++) {
            Page page = MockPageFactory.generatePage(i+1);
            Files.write(RELATION_FILE_PATH, page.serialize(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        }

        Relation relation = new Relation(RELATION_FILE_PATH, Constants.PAGE_SIZE);
        Iterator<Page> pageIterator = relation.getPageIterator();

        while(pageIterator.hasNext()) {
            Page page = pageIterator.next();

            System.out.printf("\nPAGE %d:\n", page.header.pageIdentifier);

            System.out.println("\nHEADER:");
            String headerString = Arrays.stream(page.columnMetadataArray.metadataArray).map(column -> column.columnNumber + "-" + column.attributeType).collect(Collectors.joining(", "));
            System.out.println(headerString);

            System.out.println("\nTUPLES:");

            Iterator<Tuple> tuplesIterator = page.getTuplesIterator();

            while(tuplesIterator.hasNext()) {
                Tuple tuple = tuplesIterator.next();
                String tupleString = tuple.attributeList.stream().map(attribute -> attribute.getValue().toString()).collect(Collectors.joining(":"));
                System.out.println(tupleString);
            }

            System.out.println("\n---------------------------------------------------------");
        }
    }
}