package org.example.entities.pagedirectory;

import org.example.util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public class PageDirectory {
    private final Path filePath;
    private final int pageSize;
    private final PageDirectoryHeader header;

    public PageDirectory(Path filePath, int pageSize) {
        this.filePath = filePath;
        this.pageSize = pageSize;
        this.header = this.readExistingPageOrConstructNewPage();
    }

    private PageDirectoryHeader readExistingPageOrConstructNewPage() {
        try {
            long fileSize = Files.size(this.filePath);
            boolean headerExists = fileSize > PageDirectoryHeader.getSerializedLength();

            if(headerExists) {
                byte[] headerBytes = ByteUtil.readNBytes(this.filePath, PageDirectoryHeader.getSerializedLength(), 0);
                return PageDirectoryHeader.deserialize(headerBytes);
            }
            else {
                return this.createNewHeader();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PageDirectoryHeader createNewHeader() throws IOException {
        PageDirectoryHeader newHeader = new PageDirectoryHeader();
        this.writeHeader(newHeader);
        return newHeader;
    }

    public PageDirectoryPage addNewPage() throws IOException {
        this.header.incrementPageCount();
        this.writeHeader(this.header);

        int pageNumber = this.header.getPageCount() - 1;
        PageDirectoryPage newPage = new PageDirectoryPage();
        this.writeNthPage(pageNumber, newPage);

        return newPage;
    }

    public int getPageCount() {
        return this.header.getPageCount();
    }

    public PageDirectoryPage readNthPage(int n) throws IOException {
        int pageOffsetInRelation = this.pageSize * n;
        byte[] pageBytes = ByteUtil.readNBytes(this.filePath, this.pageSize, pageOffsetInRelation);
        return PageDirectoryPage.deserialize(pageBytes);
    }

    public void writeNthPage(int n, PageDirectoryPage page) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.filePath.toFile(), "rw")) {
            byte[] pageBytes = page.serialize();

            int pageOffsetInRelation = this.pageSize * n;
            randomAccessFile.seek(pageOffsetInRelation);
            randomAccessFile.write(pageBytes);
        }
    }

    private void writeHeader(PageDirectoryHeader header) throws IOException {
        byte[] headerBytes = header.serialize();
        ByteUtil.writeNBytes(this.filePath, PageDirectoryHeader.getSerializedLength(), 0, headerBytes);
    }
}
