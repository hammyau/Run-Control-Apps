package org.genevaers.extractgenerator;

import java.io.FileWriter;

import org.genevaers.extractgenerator.codegenerators.Extractor;
import org.genevaers.extractgenerator.codegenerators.ExtractorEntry;
import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.recordreader.RecordFileWriter;

public class SelectTextExtract implements Extractor {
    public void processRecord(byte[] src, byte[] target, RecordFileWriter outWriter) {
        if(src[0] < 'H') {
            System.arraycopy(src, 5, target, 2, 7);
            outWriter.getRecordToFill().bytes.position(9);
            outWriter.writeAndClearTheRecord();
        }
    }

    @Override
    public ExtractorEntry processRecord(LTRecord lt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processRecord'");
    }
}
