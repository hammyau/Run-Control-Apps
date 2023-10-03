package org.genevaers.genevaio.ltfile;

/*
 * Copyright Contributors to the GenevaERS Project. SPDX-License-Identifier: Apache-2.0 (c) Copyright IBM Corporation 2008.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.File;
import java.io.IOException;

import org.genevaers.genevaio.fieldnodes.RecordNode;
import org.genevaers.genevaio.recordreader.RecordFileReaderWriter;
import org.genevaers.genevaio.recordreader.RecordFileReaderWriter.FileRecord;
import org.genevaers.repository.components.enums.LtRecordType;

public class XLTFileReader {

	private LTRecordReader recordReader = new LTRecordReader();
	private RecordNode recordsRoot;
	private boolean compare;
	
	private RecordFileReaderWriter rr;
	private File ltFile;
	private int numrecords;
	
	private LogicTable  logicTable = new LogicTable ();
	private boolean charSetRead = false;

	public LogicTable readLT() throws Exception {
		rr = new RecordFileReaderWriter();
		rr.readRecordsFrom(ltFile);
		FileRecord rec = rr.readRecord();
		while (rr.isAtFileEnd() == false) {
			numrecords++;
			addToLTFromRecord(rec);
			rec.bytes.clear();
			rec = rr.readRecord();
		}
		return logicTable;
	}

	private void addToLTFromRecord(FileRecord rec) throws Exception {
		determineCharacterSet(rec);
        int recType = rec.bytes.getInt(30);
        if(recType == LtRecordType.HD.ordinal()) {
			addHD(rec);
        } else if(recType == LtRecordType.NV.ordinal()) {
			addNV(rec);
        } else if(recType == LtRecordType.F0.ordinal()) {
			addF0(rec);
        } else if(recType == LtRecordType.F1.ordinal()) {
			addF1(rec);
        } else if(recType == LtRecordType.F2.ordinal()) {
			addF2(rec);
        } else if(recType == LtRecordType.RE.ordinal()) {
			addRE(rec);
        } else if(recType == LtRecordType.WR.ordinal()) {
			addWR(rec);
        } else if(recType == LtRecordType.CC.ordinal()) {
			addCC(rec);
        } else if(recType == LtRecordType.NAME.ordinal()) {
			addNAME(rec);
        } else if(recType == LtRecordType.NAMEF1.ordinal()) {
			addNAMEF1(rec);
        } else if(recType == LtRecordType.NAMEF2.ordinal()) {
			addNAMEF2(rec);
        } else if(recType == LtRecordType.NAMEVALUE.ordinal()) {
			addNAMEVALUE(rec);
        } else if(recType == LtRecordType.GENERATION.ordinal()) {
			addGeneration(rec);
        } else {

        }
		buildTreeIfNeeded();

	}

	private void buildTreeIfNeeded() {
		if(recordsRoot != null) {
			LTFileObject ltRec = (LTFileObject) logicTable.getLastEntry();
			ltRec.addRecordNodes(recordsRoot, compare);
		}
	}

	public void setCompare(boolean compare) {
		this.compare = compare;
	}

	public void setRecordsRoot(RecordNode recordsRoot) {
		this.recordsRoot = recordsRoot;
	}

	public RecordNode getRecordsRoot() {
		return recordsRoot;
	}

	public boolean isCompare() {
		return compare;
	}

    private void determineCharacterSet(FileRecord rec) {
        if(charSetRead == false) {
			if(rec.bytes.get(34) == 1) {
				recordReader.setASCIItext(true);;
			} else {
				recordReader.setEBCDICText();
			}
			charSetRead = true;
		}
    }

	private void addGeneration(FileRecord rec) throws Exception {
		LogicTableGeneration genr = new LogicTableGeneration();
		//Look ahead to get the text format
		genr.readRecord(recordReader, rec);
		logicTable.add(genr);
	}

	private void addNAMEF2(FileRecord rec) throws Exception {
		LogicTableNameF2 nameF2r = new LogicTableNameF2();
		nameF2r.readRecord(recordReader, rec);
		logicTable.add(nameF2r);
	}

	private void addNAMEF1(FileRecord rec) throws Exception {
		LogicTableNameF1 nameF1r = new LogicTableNameF1();
		nameF1r.readRecord(recordReader, rec);
		logicTable.add(nameF1r);
	}

	private void addNAMEVALUE(FileRecord rec) throws Exception {
		LogicTableNameValue nameVr = new LogicTableNameValue();
		nameVr.readRecord(recordReader, rec);
		logicTable.add(nameVr);
	}

	private void addNAME(FileRecord rec) throws Exception {
		LogicTableName namer = new LogicTableName();
		namer.readRecord(recordReader, rec);
		logicTable.add(namer);
	}

	private void addCC(FileRecord rec) throws Exception {
		LogicTableCC ccr = new LogicTableCC();
		ccr.readRecord(recordReader, rec);
		logicTable.add(ccr);
	}

	private void addWR(FileRecord rec) throws Exception {
		LogicTableWR wrr = new LogicTableWR();
		wrr.readRecord(recordReader, rec);
		logicTable.add(wrr);
	}

	private void addRE(FileRecord rec) throws Exception {
		LogicTableRE rer = new LogicTableRE();
		rer.readRecord(recordReader, rec);
		logicTable.add(rer);
	}

	private void addF2(FileRecord rec) throws Exception {
		LogicTableF2 f2r = new LogicTableF2();
		f2r.readRecord(recordReader, rec);
		logicTable.add(f2r);
	}

	private void addF1(FileRecord rec) throws Exception {
		LogicTableF1 f1r = new LogicTableF1();
		f1r.readRecord(recordReader, rec);
		logicTable.add(f1r);
	}

	private void addF0(FileRecord rec) throws Exception {
		LogicTableF0 f0r = new LogicTableF0();
		f0r.readRecord(recordReader, rec);
		logicTable.add(f0r);
	}

	private void addHD(FileRecord rec) throws Exception {
		LogicTableHD hdr = new LogicTableHD();
		hdr.readRecord(recordReader, rec);
		logicTable.add(hdr);
	}

	private void addNV(FileRecord rec) throws Exception {
		LogicTableNV nvr = new LogicTableNV();
		nvr.readRecord(recordReader, rec);
		logicTable.add(nvr);
	}

	public void open(String name) {
		ltFile = new File(name);
	}

	public LogicTable makeLT() {
		try {
			readLT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logicTable;
	}

	public int getNumberOfRecords() {
		return numrecords;
	}

}
