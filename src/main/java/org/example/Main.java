package org.example;

import org.example.entities.pagedirectory.PageDirectory;
import org.example.entities.relation.Page;
import org.example.entities.relation.PageTuple;
import org.example.entities.relation.Relation;
import org.example.util.MockPageFactory;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        SecureRandom secureRandom = new SecureRandom();
        int directoryFileId = secureRandom.nextInt(Integer.MAX_VALUE);
        Files.write(Constants.RELATION_FILE_PATH, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Constants.PAGE_DIRECTORY_FILE_PATH, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for(int i=0; i<10; i++) {
            int pageId = secureRandom.nextInt(Integer.MAX_VALUE);
            int pageNumber = i + 1; // starts at 1
            Page page = MockPageFactory.generatePage(pageId, directoryFileId, pageNumber);
            Files.write(Constants.RELATION_FILE_PATH, page.serialize(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        }

        Relation relation = new Relation(Constants.RELATION_FILE_PATH, Constants.PAGE_SIZE);
        Iterator<Page> pageIterator = relation.getPageIterator();

        int pageNumber = 0;

        while(pageIterator.hasNext()) {
            Page page = pageIterator.next();

            ArrayList<PageTuple> tuplesList = new ArrayList<>();
            Iterator<PageTuple> tuplesIterator = page.getTuplesIterator();
            while(tuplesIterator.hasNext()) {
                tuplesList.add(tuplesIterator.next());
            }

            System.out.printf("\nPAGE %d: (%d)\n", ++pageNumber, page.header.pageIdentifier);

            System.out.println("\nHEADER:");
            String headerString = Arrays.stream(page.columnMetadataArray.metadataArray).map(column -> column.columnNumber + "-" + column.attributeType).collect(Collectors.joining(", "));
            System.out.println(headerString);

            System.out.printf("\nTUPLES: %d\n", tuplesList.size());
            String format = "%-32s %-7s %-80s%n";
            System.out.printf(format, "Name", "Age", "Address");

            for(PageTuple tuple: tuplesList) {
                List<String> values = tuple.attributeList.stream().map(attribute -> attribute.getValue().toString()).toList();
                System.out.printf(format, values.toArray());
            }

            System.out.println("\n---------------------------------------------------------");
        }

        System.out.println("Exiting...");

        PageDirectory.getInstance().stopScheduler();
    }
}