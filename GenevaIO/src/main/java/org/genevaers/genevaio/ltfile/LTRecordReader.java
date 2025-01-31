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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.ibm.as400.access.AS400Text;

import org.genevaers.utilities.GersConfigration;

public class LTRecordReader {
	protected byte[] stringBuffer = new byte[300];
	protected byte[] paddingBuffer = new byte[1300];
	
	private static boolean asciiText = true;

	public static void setEBCDICText() {
		asciiText = false;
	}
	
	protected  boolean isEBCDIC() {
		return asciiText==false;
	}
	
	protected  String ebcdicToAscii(byte[] buffer, Charset charSet, int nameLen)  throws Exception {
		  AS400Text textConverter = new AS400Text(nameLen, GersConfigration.getZosCodePage());
		  return ((String)textConverter.toObject(buffer)).substring(0, nameLen);
	}

	//convert only length characters
	protected  String convertStringIfNeeded(byte[] buffer, int nameLen) throws Exception {
		String retStr;
		if(isEBCDIC()) {
			retStr = ebcdicToAscii(buffer, StandardCharsets.ISO_8859_1, nameLen);
		} else {
			retStr = new String(buffer, 0, nameLen, StandardCharsets.ISO_8859_1);
		}
		return retStr ;
	}
	
	//Convert the full buffer 
	protected  String convertStringIfNeeded(byte[] buffer) throws Exception {
		String retStr;
		if(isEBCDIC()) {
			retStr = ebcdicToAscii(buffer, StandardCharsets.ISO_8859_1, buffer.length);
		} else {
			retStr = new String(buffer, StandardCharsets.ISO_8859_1);
		}
		return retStr ;
	}

	public byte[] getStringBuffer() {
		return stringBuffer;
	}

	public byte[] getCleanStringBuffer(int len) {
		Arrays.fill(stringBuffer, 0, len+4, (byte)0);
		return stringBuffer;
	}

	public void setStringBuffer(byte[] stringBuffer) {
		this.stringBuffer = stringBuffer;
	}


	public byte[] getPaddingBuffer() {
		return paddingBuffer;
	}

	public void setPaddingBuffer(byte[] paddingBuffer) {
		this.paddingBuffer = paddingBuffer;
	}

	public static boolean isASCIItext() {
		return asciiText;
	}

	public static void setASCIItext(boolean aSCIItext) {
		asciiText = aSCIItext;
	}

	public void clearPadding() {
		paddingBuffer = new byte[1300];
	}

}
