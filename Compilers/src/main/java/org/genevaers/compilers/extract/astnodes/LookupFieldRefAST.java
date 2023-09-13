package org.genevaers.compilers.extract.astnodes;

import org.genevaers.genevaio.ltfactory.LtFactoryHolder;
import org.genevaers.genevaio.ltfactory.LtFuncCodeFactory;
import org.genevaers.genevaio.ltfile.LTFileObject;
import org.genevaers.genevaio.ltfile.LTRecord;

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


import org.genevaers.genevaio.ltfile.LogicTableArg;
import org.genevaers.genevaio.ltfile.LogicTableF0;
import org.genevaers.genevaio.ltfile.LogicTableF1;
import org.genevaers.genevaio.ltfile.LogicTableF2;
import org.genevaers.repository.Repository;
import org.genevaers.repository.components.LRField;
import org.genevaers.repository.components.LogicalRecord;
import org.genevaers.repository.components.LookupPath;
import org.genevaers.repository.components.enums.DataType;
import org.genevaers.repository.components.enums.DateCode;
import org.genevaers.repository.components.enums.ExtractArea;

public class LookupFieldRefAST extends LookupPathAST implements Assignable, CalculationSource, Concatable{

    private LRField ref;

    public LookupFieldRefAST() {
        type = ASTFactory.Type.LOOKUPFIELDREF;
    }

 
    public void resolveField(LookupPath lk, String fieldName) {
        //get the targer LR
        //And look for the field in it
        lookup = lk;
        LogicalRecord targLR = lk.getTargetLR();
        LRField fld = targLR.findFromFieldsByName(fieldName);
        if(fld != null) {       
            ref = fld;
            Repository.getJoinViews().addJLTViewField(lookup, fld);
        } else {
            ErrorAST err = (ErrorAST) ASTFactory.getNodeOfType(ASTFactory.Type.ERRORS);
            err.addError("Unkown field " + fieldName);
            addChildIfNotNull(err);
        }
    }

    public LRField getRef() {
        return ref;
    }
    
    public void populateArg(LogicTableArg arg1) {
        arg1.setDecimalCount(ref.getNumDecimalPlaces());
        arg1.setFieldContentId(ref.getDateTimeFormat());
        arg1.setFieldFormat(ref.getDatatype());
        arg1.setFieldId(ref.getComponentId());
        //TODO the start pos is dependent on extract type
        arg1.setStartPosition(ref.getStartPosition());
        arg1.setFieldLength(ref.getLength());
        arg1.setJustifyId(ref.getJustification());
        arg1.setSignedInd(ref.isSigned());
        arg1.setValueLength(0);
        arg1.setPadding2("");  //This seems a little silly
    }

    @Override
    public LTFileObject getAssignmentEntry(ColumnAST lhs, ExtractBaseAST rhs) {
        getLkEmitter().emitJoin(this, false);
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        //An issue here is that the original ref field has been mapped to a generated field
        //That is within the RED LR
        //We need to use that for arg1
        //How do we get it?
        //ask the repo to find it
        //And this is then seriously messed up since there is a mixture
        //of old and new used in the dtl function code.
        //The arg1 LF LR and field ID refer to the orignal
        //but the start position etc refer to the RED field
        LRField redField = Repository.getREDfieldFrom(lookup, ref);
        if(currentViewColumn.getExtractArea() == ExtractArea.AREADATA) {
            LogicTableF2 dtl = (LogicTableF2) fcf.getDTL(redField, currentViewColumn);
            LogicTableArg arg1 = dtl.getArg1();
            arg1.setLogfileId(lookup.getTargetLFID());
            arg1.setLrId(lookup.getTargetLRID());
            arg1.setFieldId(ref.getComponentId());
            arg1.setFieldFormat(getDataType());
            arg1.setFieldContentId(getDateCode());
            LogicTableArg arg2 = dtl.getArg2();
            flipDataTypeIfFieldAlphanumeric(arg1, arg2);
            ltEmitter.addToLogicTable((LTRecord)dtl);
        } else {
            LogicTableF2 skl = (LogicTableF2) fcf.getSKL(redField, currentViewColumn);
            skl.getArg1().setLogfileId(lookup.getTargetLFID());
            LogicTableArg arg1 = skl.getArg1();
            arg1.setLogfileId(lookup.getTargetLFID());
            arg1.setLrId(lookup.getTargetLRID());
            arg1.setFieldId(ref.getComponentId());
            arg1.setFieldFormat(getDataType());
            arg1.setFieldContentId(getDateCode());
            LogicTableArg arg2 = skl.getArg2();
            flipDataTypeIfFieldAlphanumeric(arg1, arg2);
            ltEmitter.addToLogicTable((LTRecord)skl);
        }
        emitLookupDefault();
        return null;
    }

    @Override
    public LTFileObject emitSetFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        ltEmitter.addToLogicTable((LTRecord)fcf.getSETL("", ref));
        return null;
    }

    @Override
    public LTFileObject emitAddFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        ltEmitter.addToLogicTable((LTRecord)fcf.getADDL("", ref));
        return null;
    }

    @Override
    public LTFileObject emitSubFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        ltEmitter.addToLogicTable((LTRecord)fcf.getSUBL("", ref));
        return null;
    }

    @Override
    public LTFileObject emitMulFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        ltEmitter.addToLogicTable((LTRecord)fcf.getMULL("", ref));
        return null;
    }

    @Override
    public LTFileObject emitDivFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        ltEmitter.addToLogicTable((LTRecord)fcf.getDIVL("", ref));
        return null;
    }

    private void emitLookupDefault() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        //Emit goto followed by correct DTC
        LogicTableF0 ltgoto = (LogicTableF0) fcf.getGOTO();
        ltEmitter.addToLogicTable((LTRecord)ltgoto);

        //we could let the fcf auto correc the DT type base on the column?
        switch(currentViewColumn.getExtractArea()) {
            case AREADATA:
            if(currentViewColumn.getDataType() == DataType.ALPHANUMERIC) {
                ltEmitter.addToLogicTable((LTRecord)fcf.getDTC("", currentViewColumn));
            } else {
                ltEmitter.addToLogicTable((LTRecord)fcf.getDTC("0", currentViewColumn));
            }
            break;
            
            case AREACALC:
            if(currentViewColumn.getDataType() == DataType.ALPHANUMERIC) {
                ltEmitter.addToLogicTable((LTRecord)fcf.getCTC("", currentViewColumn));
            } else {
                ltEmitter.addToLogicTable((LTRecord)fcf.getCTC("0", currentViewColumn));
            }
                break;
            case INVALID:
                break;
            case SORTKEY:
            if(currentViewColumn.getDataType() == DataType.ALPHANUMERIC) {
                ltEmitter.addToLogicTable((LTRecord)fcf.getSKC("", currentViewColumn));
            } else {
                ltEmitter.addToLogicTable((LTRecord)fcf.getSKC("0", currentViewColumn));
            }
                break;
            case SORTKEYTITLE:
                break;
            default:
                break;
    }
        ltgoto.setGotoRow1(ltEmitter.getNumberOfRecords());
    }

    @Override
    public DataType getDataType() {
        // Casting may set the format to be something we are not
        return overriddenDataType != DataType.INVALID ? overriddenDataType : ref.getDatatype();
   }

    @Override
    public DateCode getDateCode() {
        return (overriddenDateCode != null) ? overriddenDateCode : ref.getDateTimeFormat();
    }

    private void flipDataTypeIfFieldAlphanumeric(LogicTableArg arg, LogicTableArg arg2) {
        if(arg2.getFieldFormat() != DataType.ALPHANUMERIC && arg.getFieldFormat() == DataType.ALPHANUMERIC) {
            arg.setFieldFormat(DataType.ZONED);
        }
        if(arg2.getFieldFormat() == DataType.ALPHANUMERIC && arg.getFieldFormat() != DataType.ALPHANUMERIC) {
            arg2.setFieldFormat(DataType.ZONED);
        }
    }


    @Override
    public short getConcatinationEntry(ColumnAST col, ExtractBaseAST rhs, short start) {
        getAssignmentEntry(col, rhs);
        //now we need to fixup the col start and length
        //for both the assigned entry and the default
        //The last ltemitter entry will be the DTC
        //Last - 2 will be the DTL
        int numRecords = ltEmitter.getNumberOfRecords();
        LogicTableF1 dtc = (LogicTableF1) ltEmitter.getLogicTable().getFromPosition(numRecords -1);
        LogicTableF2 dtl = (LogicTableF2) ltEmitter.getLogicTable().getFromPosition(numRecords -3);
        fixupDtl(dtl, start);
        return fixupDtc(dtc, start, dtl.getArg1().getFieldLength());
    }

    private short fixupDtc(LogicTableF1 dtc, short start, short len) {
        LogicTableArg arg = dtc.getArg();
        arg.setStartPosition(start);
        arg.setFieldLength(len);
        return (short) len;
    }

    private short fixupDtl(LogicTableF2 dtl, short start) {
        LogicTableArg arg1 = ((LogicTableF2)dtl).getArg1();
        LogicTableArg arg2 = ((LogicTableF2)dtl).getArg2();
        arg2.setStartPosition(start);
        arg2.setFieldLength(arg1.getFieldLength());
        return arg1.getFieldLength();
    }


    @Override
    public short getLeftEntry(ColumnAST col, ExtractBaseAST rhs, short length) {
        getAssignmentEntry(col, rhs);
        int numRecords = ltEmitter.getNumberOfRecords();
        LogicTableF1 dtc = (LogicTableF1) ltEmitter.getLogicTable().getFromPosition(numRecords -1);
        LogicTableF2 dtl = (LogicTableF2) ltEmitter.getLogicTable().getFromPosition(numRecords -3);
        LogicTableArg arg1 = ((LogicTableF2)dtl).getArg1();
        short fieldlen = arg1.getFieldLength();
        if(length < fieldlen) { 
            arg1.setFieldLength(length);
        } else {
            //Error 
        }
        return length;
    }


    @Override
    public short getRightEntry(ColumnAST col, ExtractBaseAST rhs, short length) {
        getAssignmentEntry(col, rhs);
        int numRecords = ltEmitter.getNumberOfRecords();
        LogicTableF1 dtc = (LogicTableF1) ltEmitter.getLogicTable().getFromPosition(numRecords -1);
        LogicTableF2 dtl = (LogicTableF2) ltEmitter.getLogicTable().getFromPosition(numRecords -3);
        LogicTableArg arg1 = ((LogicTableF2)dtl).getArg1();
        short fieldlen = arg1.getFieldLength();
        if(length < fieldlen) { 
            arg1.setStartPosition((short)(arg1.getStartPosition() + fieldlen - length));
            arg1.setFieldLength(length);
        } else {
            //Error 
        }
        return length;
    }


    @Override
    public short getSubstreEntry(ColumnAST col, ExtractBaseAST rhs, short start, short length) {
        getAssignmentEntry(col, rhs);
        int numRecords = ltEmitter.getNumberOfRecords();
        LogicTableF1 dtc = (LogicTableF1) ltEmitter.getLogicTable().getFromPosition(numRecords -1);
        LogicTableF2 dtl = (LogicTableF2) ltEmitter.getLogicTable().getFromPosition(numRecords -3);
        LogicTableArg arg1 = ((LogicTableF2)dtl).getArg1();
        short fieldlen = arg1.getFieldLength();
        if(length < fieldlen) { 
            arg1.setStartPosition((short)(arg1.getStartPosition() + start));
            arg1.setFieldLength(length);
        } else {
            //Error 
        }
        return length;
    }
}
