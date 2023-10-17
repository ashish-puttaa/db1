package org.example;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Constants {
    public static final int PAGE_SIZE = 4096;
    public static final byte NULL = -1;

    public static final Path RELATION_FILE_PATH = Path.of("relation-file-1");
    public static final Path PAGE_DIRECTORY_FILE_PATH = FileSystems.getDefault().getPath("page-directory-file-1");
    public static final int PAGE_DIRECTORY_BUFFER_POOL_SIZE = PAGE_SIZE * 5;
}
