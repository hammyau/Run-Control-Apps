package org.genevaers.genevaio.vdpfile;

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


import java.io.FileWriter;
import java.io.IOException;

import org.genevaers.genevaio.fieldnodes.FieldNodeBase;
import org.genevaers.genevaio.recordreader.RecordFileReaderWriter;
import org.genevaers.genevaio.recordreader.RecordFileReaderWriter.FileRecord;

public abstract class VDPFileObject {

	protected static String spaces;
	
	public abstract void readRecord(VDPFileRecordReader reader, FileRecord rec) throws Exception;

	public abstract void addRecordNodes(FieldNodeBase root, boolean compare);
	public abstract void writeCSV(FileWriter csvFile) throws IOException;
	public abstract void writeCSVHeader(FileWriter csvFile) throws IOException;
	public abstract void fillTheWriteBuffer(RecordFileReaderWriter rw) throws Exception;

	public static void setSpaces(String spaces) {
		VDPFileObject.spaces = spaces;
	}

}
