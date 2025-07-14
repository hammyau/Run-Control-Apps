package org.genevaers.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.genevaers.genevaio.recordreader.FileRecord;
import org.genevaers.genevaio.recordreader.RecordFileReader;
import org.genevaers.genevaio.recordreader.RecordFileReaderWriter;
import org.genevaers.genevaio.recordreader.RecordFileWriter;

import com.google.common.flogger.FluentLogger;

public class PERun {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private List<String> inputDDnames = new ArrayList<>();  //Find and keep VDP Records?
    private List<String> outputDDnames = new ArrayList<>();  //Find and keep VDP Records?

    private File inputFile;

    private RecordFileReader rr;

    private int numrecords;

    private File outFile;

    private RecordFileWriter outWriter;

    public void execute() {
        logger.atInfo().log("Setup IO");
        setupIO();
        logger.atInfo().log("Read records");
        readWrite();
        logger.atInfo().log("close IO");
    }

    private void readWrite() {
        openInput(Paths.get(inputDDnames.get(0)));
        try {
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

    private void openOutput(Path outpath) {
        outFile = outpath.toFile();
    }

    private void closeInput() {
        //Anything we need to do before closing?
        rr.close();
    }

    private void setupIO() {
        //Where do we read from... ddnames defined in view sources
        //dummy 
        inputDDnames.add("ALLTYPE");
        outputDDnames.add("DUMMYOUT");
        //Where do we write too? ddnames defined in view
    }
    
	public void openInput(Path ipp) {
			inputFile = ipp.toFile();
	}
	
	private void readInput() throws Exception {
		rr = RecordFileReaderWriter.getReader();
		rr.readRecordsFrom(inputFile);
		FileRecord rec = rr.readRecord();
		while (rr.isAtFileEnd() == false) {
			numrecords++;
			processRecord(rec);
			rec.bytes.clear();
			rec = rr.readRecord();
		}
		//addVDPRecordToRepo(rec);
		logger.atInfo().log("Read %d records", numrecords);
		rr.close();
	}

    private void processRecord(FileRecord rec) throws Exception {
        logger.atInfo().log("Do something with the record");
        writeTheRecord();
    }

	public void openOutput(String name) throws IOException {
		outFile = new File(name);
		outWriter = RecordFileReaderWriter.getWriter();
        outWriter.writeRecordsTo(outFile);
	}

	private void writeTheRecord() throws Exception {
        FileRecord record = outWriter.getRecordToFill();
        String data = new String("HelloWorld");
        record.length = (short)(data.length() + 2);
        record.bytes.putShort(record.length);
        record.bytes.put(data.getBytes());
		outWriter.writeAndClearTheRecord();
	}

}
