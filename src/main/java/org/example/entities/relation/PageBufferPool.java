package org.example.entities.relation;

import org.example.Constants;
import org.example.entities.common.BufferPool;
import org.example.entities.pagedirectory.PageDirectoryManager;
import org.example.entities.pagedirectory.PageDirectoryRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageBufferPool extends BufferPool<Integer, Page> {
    private static final Logger LOGGER = Logger.getLogger(PageBufferPool.class.getName());

    public PageBufferPool(int capacity) {
        super(capacity);
    }

    @Override
    protected Optional<Page> read(Integer pageIdentifier) {
        return this.readPageFromDisk(pageIdentifier);
    }

    @Override
    protected void handleBufferEviction(Integer pageIdentifier, Page page) {
        this.handleDirtyPages(pageIdentifier, page);
    }

    @Override
    protected void handleBufferSchedule(Set<Map.Entry<Integer, Page>> entries) {
        entries.forEach(entry -> this.handleDirtyPages(entry.getKey(), entry.getValue()));
    }

    private Optional<Page> readPageFromDisk(int pageIdentifier) {
        Optional<PageDirectoryRecord> optionalRecord = PageDirectoryManager.getInstance().getRecord(pageIdentifier);

        try {
            if(optionalRecord.isPresent()) {
                PageDirectoryRecord record = optionalRecord.get();

                int databaseFile = record.databaseFileId();
                int pageNumber = record.pageNumber();

                Path databaseFilePath = Path.of(String.valueOf(databaseFile));
                Relation relation = new Relation(databaseFilePath, Constants.PAGE_SIZE);

                Page page = relation.readNthPage(pageNumber);
                return Optional.of(page);
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.INFO, "Exception while reading page from disk using identifier", e);
        }

        return Optional.empty();
    }


    private void writePageToDisk(int pageIdentifier, Page page) {
        Optional<PageDirectoryRecord> optionalRecord = PageDirectoryManager.getInstance().getRecord(pageIdentifier);

        try {
            if(optionalRecord.isPresent()) {
                PageDirectoryRecord record = optionalRecord.get();

                int databaseFile = record.databaseFileId();
                int pageNumber = record.pageNumber();

                Path databaseFilePath = Path.of(String.valueOf(databaseFile));
                Relation relation = new Relation(databaseFilePath, Constants.PAGE_SIZE);

                relation.writeNthPage(pageNumber, page);
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.INFO, "Exception while writing page to disk using page identifier", e);
        }
    }

    private void handleDirtyPages(int pageIdentifier, Page page) {
        if(page.isDirty()) {
            this.writePageToDisk(pageIdentifier, page);
        }
    }
}
