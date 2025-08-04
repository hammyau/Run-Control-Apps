package org.genevaers.engine.extractor;

import java.nio.ByteBuffer;

import org.genevaers.genevaio.recordreader.RecordFileWriter;

public class SelectTextExtract implements Extractor {
    public void processRecord(byte[] src, ByteBuffer target, RecordFileWriter outWriter) {
        if(src[0] < 'H') {
            System.arraycopy(src, 5, target, 2, 7);
            outWriter.getRecordToFill().bytes.position(9);
            outWriter.writeAndClearTheRecord();
        }
    }

    @Override
    public int getOutputLen() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOutputLen'");
    }

    @Override
    public int getLrLen() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLrLen'");
    }
}
