package org.example.types;

import org.example.Util;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public List<Page> readAllPages() throws IOException {
        File file = this.path.toFile();
        byte[] bytesArray = new byte[(int) file.length()];

        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            int readStatus = fileInputStream.read(bytesArray);
        }
        catch (FileNotFoundException fileNotFoundException) {
            LOGGER.log(Level.SEVERE, "Relation file not found: ", fileNotFoundException);
        }

        List<byte[]> tupleBytesList = Util.splitByteArray(bytesArray, this.pageSize);

        return tupleBytesList.stream()
                .map(tupleBytes -> Page.fromBytes(tupleBytes, this.attributeTypesList))
                .collect(Collectors.toList());
    }

    public Page readNthPageBytes(int n) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(this.path.toFile(), "r")) {
            byte[] pageBytes = new byte[this.pageSize];

            int pageOffsetInRelation = this.pageSize * (n - 1);
            randomAccessFile.seek(pageOffsetInRelation);
            randomAccessFile.readFully(pageBytes);

            return Page.fromBytes(pageBytes, this.attributeTypesList);
        }
    }
}
