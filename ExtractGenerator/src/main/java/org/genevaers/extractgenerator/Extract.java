package org.genevaers.extractgenerator;

import java.io.FileWriter;
import java.nio.ByteBuffer;

import org.genevaers.extractgenerator.codegenerators.ExtractRecordGenerator;
import org.genevaers.extractgenerator.codegenerators.ExtractorEntry;
import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.recordreader.RecordFileWriter;
import org.genevaers.utilities.GersCodePage;

public class Extract implements ExtractRecordGenerator {
    public void processRecord(byte[] src, byte[] target, RecordFileWriter outWriter) {
        if(src[29] < 11) {
            byte[] stringBuffer = new byte[19];
            System.arraycopy(src, 0, stringBuffer, 0, 19);
            System.arraycopy(GersCodePage.ebcdicToAscii(stringBuffer), 0, target, 2, 19);
            target[21] = src[30];
            outWriter.getRecordToFill().bytes.position(20);
            outWriter.writeAndClearTheRecord();
        }
    }

    @Override
    public ExtractorEntry processRecord(LTRecord lt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processRecord'");
    }
}
