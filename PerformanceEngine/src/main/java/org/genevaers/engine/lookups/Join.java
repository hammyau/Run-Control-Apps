package org.genevaers.engine.lookups;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.genevaers.genevaio.recordreader.FileRecord;

import com.google.common.flogger.FluentLogger;

public class Join {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private Map<ByteArrayKey, FileRecord> data = new HashMap<>();
    private int joinId;
    private boolean read;

    private boolean found;

    private FileRecord currentBuffer;

    private int currentRecord;

    private boolean updateRequired;

    private ByteArrayKey key; 
    
    public void addReferenceRecord(FileRecord refRecord) {
        //get the key
        FileRecord joinRecord = new FileRecord();

        ByteArrayKey ipkey = new ByteArrayKey(refRecord.bytes.array(), 0, 1);
        joinRecord.bytes.put(refRecord.bytes.array(), 0, 25);
        data.put(ipkey, joinRecord);
    }

    public void logContent() {
        Iterator<Entry<ByteArrayKey, FileRecord>> jei = data.entrySet().iterator();
        while (jei.hasNext()) {
            Entry<ByteArrayKey, FileRecord> je = jei.next();
            byte[] joinrecBuffer = new byte[24];
            FileRecord val = je.getValue();
            val.bytes.position(1);
            for(int i=0; i<24; i++) {
                joinrecBuffer[i] = val.bytes.get();
            }
            logger.atInfo().log("Key %s -> %s",je.getKey() , new String(joinrecBuffer));
        }
    }

    public void setJoinId(int joinId) {
        this.joinId = joinId;
    }

    public int getJoinId() {
        return joinId;
    }

    public boolean bufferNotUpdated() {
        return !read;
    }

    public void addToKey(byte[] src, int offset, int len) {
        //This will need to be a ByteBuffer wrapping the key array
        key = new ByteArrayKey(src, offset, len);
    }

    public FileRecord updateBuffer() {
        currentBuffer = data.get(key);
        if(currentBuffer != null) {
            currentBuffer.bytes.position(0);
        }
        return currentBuffer;
    }

    public boolean found() {
        return found;
    }

    public FileRecord getBufferForRecord(int numrecords) {
        if(currentRecord != numrecords) {
            currentRecord = numrecords;
            currentBuffer = null;
            updateRequired = true;
        }
        return currentBuffer;
    }

    public boolean updateRequired() {
        return updateRequired;
    }
}
