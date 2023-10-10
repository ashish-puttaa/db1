package org.example;

import org.example.types.Page;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;

public class Main {

    private static final int PAGE_SIZE = 4096;
    private static final Path RELATION_FILE_PATH = Path.of("relation-file-1");


    private static Page[] generatePages(int num) {
        Page[] pages = new Page[num];

        for(int i=0; i<num; i++) {
            pages[i] = Util.generateSamplePage(String.valueOf(num), PAGE_SIZE);
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
    }
}