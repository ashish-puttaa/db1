package org.example.entities.pagedirectory;

import org.example.Constants;
import org.example.entities.common.Dirtyable;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PageDirectoryPage extends Dirtyable {
    private final Map<Integer, PageDirectoryRecord> pageIdMap;

    public PageDirectoryPage() {
        this.pageIdMap = new HashMap<>();
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.PAGE_SIZE);
        byteBuffer.putInt(this.pageIdMap.size());

        for(Map.Entry<Integer, PageDirectoryRecord> entry: this.pageIdMap.entrySet()) {
            int pageId = entry.getKey();
            PageDirectoryRecord pageDirectoryRecord = entry.getValue();

            byteBuffer.putInt(pageId);
            byteBuffer.put(pageDirectoryRecord.serialize());
        }

        return byteBuffer.array();
    }

    public static PageDirectoryPage deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        PageDirectoryPage pageDirectoryPage = new PageDirectoryPage();
        int numEntries = byteBuffer.getInt();

        for(int i=0; i<numEntries; i++) {
            int pageId = byteBuffer.getInt();
            byte[] recordBytes = ByteUtil.readNBytes(byteBuffer, PageDirectoryRecord.getSerializedLength());
            PageDirectoryRecord pageDirectoryRecord = PageDirectoryRecord.deserialize(recordBytes);
            pageDirectoryPage.addMapping(pageId, pageDirectoryRecord);
        }

        pageDirectoryPage.markAsClean();
        return pageDirectoryPage;
    }

    private int getSerializedEntryLength() {
        return Integer.BYTES + PageDirectoryRecord.getSerializedLength();
    }

    public int getSerializedLength() {
        return Integer.BYTES + this.getSerializedEntryLength() * this.pageIdMap.size();
    }

    public void addMapping(int pageId, PageDirectoryRecord pageDirectoryRecord) {
        this.pageIdMap.put(pageId, pageDirectoryRecord);
        this.markAsDirty();
    }

    public boolean hasMapping(int pageId) {
        return this.pageIdMap.containsKey(pageId);
    }

    public Optional<PageDirectoryRecord> getMapping(int pageId) {
        if(this.hasMapping(pageId)) {
            return Optional.of(this.pageIdMap.get(pageId));
        }

        return Optional.empty();
    }
}
