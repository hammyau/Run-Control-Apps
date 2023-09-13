package org.genevaers.compilers.extract.astnodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.genevaers.genevaio.ltfile.LogicTableF1;
import org.genevaers.genevaio.ltfile.LogicTableF2;
import org.genevaers.genevaio.ltfile.LogicTableNameF1;
import org.genevaers.repository.components.LRField;
import org.genevaers.repository.components.LogicalRecord;
import org.genevaers.repository.components.ViewColumn;
import org.genevaers.repository.components.enums.DateCode;
import org.genevaers.repository.components.enums.ExtractArea;
import org.genevaers.repository.components.enums.DataType;

// The FormattedASTNode could/should be an inteface it implements
// Ditto the TypedASTNode
public class FieldReferenceAST extends FormattedASTNode implements Assignable, CalculationSource, Concatable {

    protected LRField ref;
    protected String name; //this is a duplication for the moment
    // the name is within the LRField

    private DataType format = DataType.INVALID; //Note that can be overridden.

    public FieldReferenceAST() {
        type = ASTFactory.Type.LRFIELD;
    }

    public void setRef(LRField ref) {
        this.ref = ref;
    }

    public LRField getRef() {
        return ref;
    }

    public String getName() {
        if(ref != null) {
            return ref.getName();
        } else {
            return name;
        }
    }

    public void resolveField(LogicalRecord lr, String fieldName) {
        // //Check for PRIOR
        // if() {
        //     stripPrior(fieldName);
        // }
        LRField fld = lr.findFromFieldsByName(fieldName);
        name = fieldName;
        if(fld != null) {       
            ref = fld;
        } else {
            ErrorAST err = (ErrorAST) ASTFactory.getNodeOfType(ASTFactory.Type.ERRORS);
            err.addError("Unknown field " + fieldName);
            addChildIfNotNull(err);
        }
    }

    private void stripPrior(String fieldName) {
        Pattern stringPattern = Pattern.compile("PRIOR(");
        Matcher m = stringPattern.matcher(fieldName);
    }

    public void populateArg(LogicTableArg arg1) {
        //Not sure we should be doing this.
        //Case arises when say a field is not found.
        //We should not be attempting to use the field 
        //it should be handled somewhere before we get here
        if(ref != null) {
            arg1.setDecimalCount(ref.getNumDecimalPlaces());
            arg1.setFieldContentId(ref.getDateTimeFormat());
            arg1.setFieldFormat(ref.getDatatype());
            arg1.setLrId(ref.getLrID());
            arg1.setFieldId(ref.getComponentId());
            //TODO the start pos is dependent on extract type
            arg1.setStartPosition(ref.getStartPosition());
            arg1.setFieldLength(ref.getLength());
            arg1.setJustifyId(ref.getJustification());
            arg1.setSignedInd(ref.isSigned());
            arg1.setValueLength(0);
            arg1.setPadding2("");  //This seems a little silly
        }
    }

    public void setDateTimeFormat(DateCode overContent) {
        overrideDateCode(overContent);
    }


    @Override
    public LTFileObject getAssignmentEntry(ColumnAST lhs, ExtractBaseAST rhs) {
        ViewColumn vc = ((ColumnAST)lhs).getViewColumn();
        LTFileObject ltEntry = null;
        LRField field = ((FieldReferenceAST)rhs).getRef();
        if(field != null) {
            ltEntry =((ColumnAST)lhs).getFieldLtEntry(field);
            LogicTableArg arg;
            if(vc.getExtractArea() == ExtractArea.AREACALC) {
                arg = ((LogicTableF1)ltEntry).getArg();
                arg.setFieldFormat(getDataType());
            } else {
                arg = ((LogicTableF2)ltEntry).getArg1();
                arg.setFieldFormat(getDataType());
                LogicTableArg arg2 = ((LogicTableF2)ltEntry).getArg2();
                flipDataTypeIfFieldAlphanumeric(arg, arg2);
            }
            arg.setLogfileId(getLtEmitter().getFileId());        
            arg.setFieldContentId(getDateCode());
        }
        return ltEntry;
    }

    @Override
    public LTFileObject emitSetFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        if(ref == null) {
            int bang = 1;
        }
        LogicTableNameF1 sete = (LogicTableNameF1) fcf.getSETE("", ref);
        sete.getArg().setLogfileId(ltEmitter.getFileId());
        ltEmitter.addToLogicTable((LTRecord)sete);
        return null;
    }

    @Override
    public LTFileObject emitAddFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        LogicTableNameF1 adde = (LogicTableNameF1) fcf.getADDE("", ref);
        adde.getArg().setLogfileId(ltEmitter.getFileId());
        ltEmitter.addToLogicTable((LTRecord)adde);
        return null;
    }

    @Override
    public LTFileObject emitSubFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        LogicTableNameF1 sube = (LogicTableNameF1) fcf.getSUBE("", ref);
        sube.getArg().setLogfileId(ltEmitter.getFileId());
        ltEmitter.addToLogicTable((LTRecord)sube);
        return null;
    }

    @Override
    public LTFileObject emitMulFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        LogicTableNameF1 mule = (LogicTableNameF1) fcf.getMULE("", ref);
        mule.getArg().setLogfileId(ltEmitter.getFileId());
        ltEmitter.addToLogicTable((LTRecord)mule);
        return null;
    }

    @Override
    public LTFileObject emitDivFunctionCode() {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        LogicTableNameF1 dive = (LogicTableNameF1) fcf.getDIVE("", ref);
        dive.getArg().setLogfileId(ltEmitter.getFileId());
        ltEmitter.addToLogicTable((LTRecord)dive);
        return null;
    }

    @Override
    public DataType getDataType() {
        // Casting may set the format to be something we are not
        if(ref != null) {
            return overriddenDataType != DataType.INVALID ? overriddenDataType : ref.getDatatype();
        } else {
            return overriddenDataType;
        }
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
        LogicTableF2 f2 = (LogicTableF2) getAssignmentEntry(col, rhs);
        LogicTableArg arg1 = ((LogicTableF2)f2).getArg1();
        LogicTableArg arg2 = ((LogicTableF2)f2).getArg2();
        arg2.setStartPosition(start);
        arg2.setFieldLength(arg1.getFieldLength());
        ltEmitter.addToLogicTable((LTRecord)f2);
        return arg1.getFieldLength();
    }

    @Override
    public short getLeftEntry(ColumnAST col, ExtractBaseAST rhs, short length) {
        LogicTableF2 f2 = (LogicTableF2) getAssignmentEntry(col, rhs);
        LogicTableArg arg1 = ((LogicTableF2)f2).getArg1();
        short fieldlen = arg1.getFieldLength();
        if(length < fieldlen) { 
            arg1.setFieldLength(length);
            ltEmitter.addToLogicTable((LTRecord)f2);
        } else {
            //Error 
        }
        return length;
    }

    @Override
    public short getRightEntry(ColumnAST col, ExtractBaseAST rhs, short length) {
        LogicTableF2 f2 = (LogicTableF2) getAssignmentEntry(col, rhs);
        LogicTableArg arg1 = ((LogicTableF2)f2).getArg1();
        short fieldlen = arg1.getFieldLength();
        if(length < fieldlen) { 
            arg1.setStartPosition((short)(arg1.getStartPosition() + fieldlen - length));
            arg1.setFieldLength(length);
            ltEmitter.addToLogicTable((LTRecord)f2);
        } else {
            //Error 
        }
        return length;
    }

    @Override
    public short getSubstreEntry(ColumnAST col, ExtractBaseAST rhs, short start, short length) {
        LogicTableF2 f2 = (LogicTableF2) getAssignmentEntry(col, rhs);
        LogicTableArg arg1 = ((LogicTableF2)f2).getArg1();
        short fieldlen = arg1.getFieldLength();
        if(length < fieldlen) { 
            arg1.setStartPosition((short)(arg1.getStartPosition() + start));
            arg1.setFieldLength(length);
            ltEmitter.addToLogicTable((LTRecord)f2);
        } else {
            //Error 
        }
        return length;
    }

}
