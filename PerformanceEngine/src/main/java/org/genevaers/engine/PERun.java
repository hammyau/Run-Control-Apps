package org.genevaers.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.genevaers.engine.extractor.Extract;
import org.genevaers.engine.extractor.Extractor;
import org.genevaers.genevaio.recordreader.FileRecord;
import org.genevaers.genevaio.recordreader.RecordFileReader;
import org.genevaers.genevaio.recordreader.RecordFileReaderWriter;
import org.genevaers.genevaio.recordreader.RecordFileWriter;
import org.genevaers.utilities.GersCodePage;
import org.genevaers.utilities.GersConfigration;
import org.genevaers.utilities.GersFilesUtils;

import com.google.common.flogger.FluentLogger;

public class PERun {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private List<String> inputDDnames = new ArrayList<>(); // Find and keep VDP Records?
    private List<String> outputDDnames = new ArrayList<>(); // Find and keep VDP Records?

    private File inputFile;

    private RecordFileReader rr;

    private int numrecords;

    private File outFile;

    private RecordFileWriter outWriter;
    protected byte[] stringBuffer = new byte[8192];
    protected byte[] paddingBuffer = new byte[1400];

    private boolean ASCIItext = true;

    private Extract extractor;

    private FileRecord outputRecord;

    public void setEBCDICText() {
        ASCIItext = false;
    }

    public void setASCIIText() {
        ASCIItext = true;
    }

    protected boolean isEBCDIC() {
        return ASCIItext == false;
    }

    // convert only length characters
    protected String convertStringIfNeeded(byte[] buffer, int nameLen) throws Exception {
        String retStr;
        if (isEBCDIC()) {
            retStr = new String(GersCodePage.ebcdicToAscii(buffer));
        } else {
            retStr = new String(buffer, 0, nameLen, StandardCharsets.ISO_8859_1);
        }
        return retStr;
    }

    public byte[] getStringBuffer() {
        return stringBuffer;
    }

    public byte[] getCleanStringBuffer(int len) {
        Arrays.fill(stringBuffer, 0, len + 4, (byte) 0);
        return stringBuffer;
    }

    public void setStringBuffer(byte[] stringBuffer) {
        this.stringBuffer = stringBuffer;
    }

    public boolean isASCIItext() {
        return ASCIItext;
    }

    public void setASCIItext(boolean aSCIItext) {
        ASCIItext = aSCIItext;
    }

    public byte[] getPaddingBuffer() {
        return paddingBuffer;
    }

    public void execute() {
        logger.atInfo().log("Read write records");
        readWrite();
        logger.atInfo().log("close IO");
    }

    private void readWrite() {
        try {
            extractor = getExtractor();
            setupIO();
            openInput(Paths.get(inputDDnames.get(0)));
            openOutput(outputDDnames.get(0));
            readInput();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        closeOutput();
        closeInput();

    }

    private void closeOutput() {
        outWriter.close();
    }

    private void closeInput() {
        // Anything we need to do before closing?
        rr.close();
    }

    private void setupIO() {
        // Where do we read from... ddnames defined in view sources
        // dummy
        logger.atInfo().log("Setup IO");
        inputDDnames.addAll(extractor.getInputDDnames());
        outputDDnames.add("DUMMYOUT");
        // Where do we write too? ddnames defined in view
    }

    public void openInput(Path ipp) {
        inputFile = ipp.toFile();
        ASCIItext = false;
        GersConfigration.setZosCodePage("IBM-1047");
    }

    private void readInput() throws Exception {
        rr = RecordFileReaderWriter.getReader();
        rr.readRecordsFrom(inputFile);
        rr.setRecLen(28);
        FileRecord rec = rr.readRecord();
        while (rr.isAtFileEnd() == false) {
            numrecords++;
            processRecord(rec);
            rec.bytes.clear();
            rec = rr.readRecord();
        }
        // addVDPRecordToRepo(rec);
        logger.atInfo().log("Read %d records", numrecords);
        rr.close();
    }

    private void processRecord(FileRecord rec) throws Exception {
        logger.atInfo().log("Do something with the record");
        extractor.processRecord(rec.bytes.array(), outputRecord.bytes, outWriter);
     }

    private Extract getExtractor() {
        try {
            Class<?> exc = Class.forName("org.genevaers.engine.extractor.XLT");
            Constructor<?>[] constructors = exc.getConstructors();
            return ((Extract) constructors[0].newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | ClassNotFoundException e) {
            logger.atSevere().log("getGersFiles failed %s", e.getMessage());
        }
        return null;
    }

    public void openOutput(String name) throws IOException {
        outFile = new File(name);
        outWriter = RecordFileReaderWriter.getWriter();
        outWriter.writeRecordsTo(outFile);
        outWriter.setReclen(extractor.getOutputLen());
        outputRecord = outWriter.getRecordToFill();
        outputRecord.length = (short) (extractor.getOutputLen());
        //outputRecord.bytes.putShort( outputRecord.length);
    }

    // private void writeTheRecord(FileRecord record) throws Exception {
    //     //FileRecord record = outWriter.getRecordToFill();
    //     // String data = new String("HelloWorld");
    //     // record.length = (short) (desc.length() + 2);
    //     // record.bytes.putShort(record.length);
    //     // record.bytes.put(desc.getBytes());
    //     outWriter.writeAndClearTheRecord();
    // }

}
