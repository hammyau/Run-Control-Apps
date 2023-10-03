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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.genevaers.genevaio.vdpfile.record.VDPRecord;
import org.genevaers.repository.Repository;
import org.genevaers.repository.components.ViewNode;

public class VDPManagementRecords {
    private VDPGenerationRecord generation;
    private VDPFormatViews formatViews;
    private VDPLookupPathTargetSet lookupPathTargetSet;
    private VDPLookupGenMap lookupPathGenerationMap;
    private VDPExtractRecordFile extractRecordFile;
    private Map<Integer, ViewManagementData> viewSpecificData = new HashMap<>();
    private VDPExtractFile ExtractOutputFile;

    public VDPGenerationRecord getViewGeneration() {
        return generation;
    }

    public void setViewGeneration(VDPGenerationRecord viewGeneration) {
        this.generation = viewGeneration;
    }

    public ViewManagementData getViewManagmentData(int id) {
        ViewManagementData vmd = viewSpecificData.get(id);
        if (vmd == null) {
            vmd = new ViewManagementData();
            viewSpecificData.put(id, vmd);
        }
        return vmd;
    }

    public VDPFormatViews getFormatViews() {
        return formatViews;
    }

    public void setFormatViews(VDPFormatViews formatViews) {
        this.formatViews = formatViews;
    }

    public VDPLookupPathTargetSet getLookupPathTargetSet() {
        return lookupPathTargetSet;
    }

    public void setLookupPathTargetSet(VDPLookupPathTargetSet lookupPathTargetSet) {
        this.lookupPathTargetSet = lookupPathTargetSet;
    }

    public VDPLookupGenMap getLookupPathGenerationMap() {
        return lookupPathGenerationMap;
    }

    public void setLookupPathGenerationMap(VDPLookupGenMap lookupPathGenerationMap) {
        this.lookupPathGenerationMap = lookupPathGenerationMap;
    }

    public VDPExtractRecordFile getExtractRecordFile() {
        return extractRecordFile;
    }

    public void setExtractRecordFile(VDPExtractRecordFile extractRecordFile) {
        this.extractRecordFile = extractRecordFile;
    }

    public void setExtractOutpuFile(VDPExtractFile vef) {
        this.ExtractOutputFile = vef;
    }

    public VDPExtractFile getExtractOutputFile() {
        return ExtractOutputFile;
    }

    public void fillExtractFileNumbersFrom(Set<Short> extractFileNubers) {
        for (Short efn : extractFileNubers) {
            extractRecordFile.getExtracts().add(efn);
        }
        extractRecordFile.setNumberOfExtracts((short) extractFileNubers.size());
    }

    public void makeFormatViewRecord() {
        formatViews = new VDPFormatViews();
        formatViews.setSequenceNbr((short)0);
        formatViews.setRecordType(VDPRecord.VDP_FORMAT_VIEWS);
        formatViews.setRecordId(VDPRecord.VDP_FORMAT_VIEWS);
        int totalFormatDTColumns = 0; // Note this is just column count
        int totalFormatSKColumns = 0;
        int totalNumStackEntries = 0;
        Iterator<ViewNode> fvi = Repository.getFormatViews().getIterator();
        while (fvi.hasNext()) {
            ViewNode fv = fvi.next();
            formatViews.getFormatViews().add(fv.getID());
            totalFormatDTColumns += fv.getNumberOfColumns();
            totalFormatSKColumns += fv.getNumberOfSortKeys();
            totalNumStackEntries += fv.getNumCalcStackEntries();
        }
        formatViews.setNumViews(Repository.getFormatViews().size());
        formatViews.setNumDTColumns(totalFormatDTColumns);
        formatViews.setNumSKColumns(totalFormatSKColumns);
        formatViews.setNumTerminatedStackEntries(totalNumStackEntries);
        formatViews.setPadding("");
    }

}
