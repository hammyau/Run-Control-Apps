package org.genevaers.genevaio.recordreader;

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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.genevaers.utilities.GersConfigration;

public abstract class RecordFileReader {
	private static boolean spacesConverted = false;
	protected static String spaces = StringUtils.repeat(" ", 1536); //!!! Don't change this length  !!!!!
	protected boolean EOFreached = false;

	public abstract void readRecordsFrom(File file) throws IOException;
	public abstract FileRecord readRecord();
	public abstract void close();

	public static String getSpaces() {
		return spaces;
	}

	public static void setSpacesEBCDIC() {
		if(spacesConverted == false) {
			Charset utf8charset = Charset.forName("UTF-8");
			Charset ebccharset = Charset.forName(GersConfigration.getZosCodePage());
			ByteBuffer inputBuffer = ByteBuffer.wrap(spaces.getBytes());
			CharBuffer data = utf8charset.decode(inputBuffer);
			spaces = new String(ebccharset.encode(data).array());	
			spacesConverted = true;
		}
	}

	public boolean isAtFileEnd() {
		return EOFreached;
	}
}
