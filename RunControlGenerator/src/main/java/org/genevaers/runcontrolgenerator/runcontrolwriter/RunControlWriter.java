package org.genevaers.runcontrolgenerator.runcontrolwriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import org.genevaers.genevaio.ltfile.LTWriter;
import org.genevaers.genevaio.ltfile.LogicTable;
import org.genevaers.genevaio.vdpfile.VDPExtractRecordFile;
import org.genevaers.genevaio.vdpfile.VDPFileWriter;
import org.genevaers.genevaio.vdpfile.VDPGenerationRecord;
import org.genevaers.genevaio.vdpfile.VDPManagementRecords;
import org.genevaers.genevaio.vdpfile.record.VDPRecord;
import org.genevaers.repository.Repository;
import org.genevaers.runcontrolgenerator.configuration.RunControlConfigration;
import org.genevaers.runcontrolgenerator.utility.Status;
import org.genevaers.utilities.GenevaLog;

import com.google.common.flogger.FluentLogger;

public class RunControlWriter {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private LogicTable extractLogicTable;
    private LogicTable joinLogicTable;
    private RunControlConfigration rcc;

	public RunControlWriter(RunControlConfigration rc) {
        rcc = rc;
	}

	public Status run() {
        GenevaLog.writeHeader("Write the run control files");
		//Emit the XLT from the compiler AST
        LTWriter xltw = new LTWriter();
        LTWriter jltw = new LTWriter();
        try {
            if(joinLogicTable.getNumberOfRecords() > 5) {
                logger.atInfo().log("Write Join Logic Table");
                jltw.write(joinLogicTable, Paths.get(rcc.getJLTFileName()));
                jltw.close();
            } else {
                logger.atInfo().log("No Join Logic Table required");
            }
            logger.atInfo().log("Write Extract Logic Table");
            xltw.write(extractLogicTable, Paths.get(rcc.getXLTFileName()));
            xltw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        VDPFileWriter vdpw = new VDPFileWriter();
        vdpw.open(rcc.getVdpFile());
        VDPManagementRecords vmrs = makeVDPManagementRecords();
        try {
            vdpw.writeVDPFrom(vmrs);
            vdpw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Status.OK;
	}

    private VDPManagementRecords makeVDPManagementRecords() {
        VDPManagementRecords vmrs = new VDPManagementRecords();
        vmrs.setViewGeneration(makeGenerationRecord());
        vmrs.setExtractRecordFile(makeExtractRecordFile());
        return vmrs;
    }

    private VDPExtractRecordFile makeExtractRecordFile() {
        VDPExtractRecordFile erf = new VDPExtractRecordFile();
        erf.setRecordType(VDPRecord.VDP_EXTRACT_RECORD_FILE);
        erf.setRecordId(25); 
        erf.setExtractPrefix("EXTR");
        //We need to have accumulated the number of extract files
        //This would be done during compiler generating WR statements
        //For the moment it is 0
        return erf;
    }

    private VDPGenerationRecord makeGenerationRecord() {
        //Need a Generation Record if nothing else
        VDPGenerationRecord gen = new VDPGenerationRecord();
        gen.setRecordType(VDPRecord.VDP_GENERATION);
        gen.setAsciiInd(System.getProperty("os.name").startsWith("z") ? false : true);
        DateFormat dateFormat1 = new SimpleDateFormat("YYYYMMdd");
        Date dt = Repository.getGenerationTime();
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        gen.setDate(dateFormat.format(dt));
        gen.setRunDate(dateFormat1.format(dt));
        gen.setTime(timeFormat.format(dt));
        gen.setDescription("Java MR91");
        gen.setPadding4("");
        gen.setPadding5("");
        return gen;
    }

    public void setExtractLogicTable(LogicTable lt) {
        extractLogicTable = lt;
    }

    public void setJoinLogicTable(LogicTable lt) {
        joinLogicTable = lt;
    }

}
