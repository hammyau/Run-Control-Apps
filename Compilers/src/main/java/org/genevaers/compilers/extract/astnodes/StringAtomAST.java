package org.genevaers.compilers.extract.astnodes;

import org.genevaers.genevaio.ltfactory.LtFactoryHolder;
import org.genevaers.genevaio.ltfactory.LtFuncCodeFactory;
import org.genevaers.genevaio.ltfile.LTFileObject;
import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.ltfile.LogicTableArg;
import org.genevaers.genevaio.ltfile.LogicTableF1;
import org.genevaers.repository.components.enums.DataType;
import org.genevaers.repository.components.enums.DateCode;
import org.genevaers.repository.components.enums.ExtractArea;

import difflib.StringUtills;

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


public class StringAtomAST extends FormattedASTNode implements GenevaERSValue, Assignable, Concatable {

    private String value;

    public StringAtomAST() {
        type = ASTFactory.Type.STRINGATOM;
    }

    public void setValue(String value) {
        this.value = value.replace("\"", "");
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getValueString() {
        return value;
    }

    @Override
    public LTFileObject getAssignmentEntry(ColumnAST lhs, ExtractBaseAST rhs) {
        LtFuncCodeFactory fcf = LtFactoryHolder.getLtFunctionCodeFactory();
        LTRecord ltr;
        if(currentViewColumn.getExtractArea() == ExtractArea.AREACALC) {
            ltr = (LTRecord)fcf.getCTC(value, currentViewColumn);
        } else if(currentViewColumn.getExtractArea() == ExtractArea.AREADATA) {
            ltr = (LTRecord)fcf.getDTC(value, currentViewColumn);
        } else {
            ltr = (LTRecord)fcf.getSKC(value, currentViewColumn);
        }
        return (LTFileObject) ltr;
    }

    @Override
    public DataType getDataType() {
        return overriddenDataType != DataType.INVALID ? overriddenDataType : DataType.ALPHANUMERIC;
    }

    @Override
    public DateCode getDateCode() {
        return (overriddenDateCode != null) ? overriddenDateCode : DateCode.NONE;
    }

    @Override
    public short getConcatinationEntry(ColumnAST col, ExtractBaseAST rhs, short start) {
        LogicTableF1 f1 = (LogicTableF1) getAssignmentEntry(col, rhs);
        LogicTableArg arg = f1.getArg();
        arg.setStartPosition(start);
        arg.setFieldLength((short)arg.getValueLength());
        ltEmitter.addToLogicTable(f1);
        return (short) arg.getValueLength();
    }

    @Override
    public short getLeftEntry(ColumnAST col, ExtractBaseAST rhs, short length) {
        LogicTableF1 f1 = (LogicTableF1) getAssignmentEntry(col, rhs);
        LogicTableArg arg = f1.getArg();
        //This is different in that we need to get the original string value and change it?
        //arg.setStartPosition(start);
        arg.setFieldLength((short)arg.getValueLength());
        short fieldlen = arg.getFieldLength();
        if(length < fieldlen) { 
            String val = ((StringAtomAST)rhs).getValue();
            arg.setValue(val.substring(0, length));
            arg.setValueLength(length);
            ltEmitter.addToLogicTable((LTRecord)f1);
        } else {
            //Error 
        }
        return length;
    }

    @Override
    public short getRightEntry(ColumnAST col, ExtractBaseAST rhs, short length) {
        LogicTableF1 f1 = (LogicTableF1) getAssignmentEntry(col, rhs);
        LogicTableArg arg = f1.getArg();
        //This is different in that we need to get the original string value and change it?
        //arg.setStartPosition(start);
        short fieldlen = arg.getFieldLength();
        if(length < fieldlen) { 
            String val = ((StringAtomAST)rhs).getValue();
            arg.setValue(val.substring(val.length() - length));
            arg.setValueLength(length);
            ltEmitter.addToLogicTable((LTRecord)f1);
        } else {
            //Error 
        }
        return length;
    }

    @Override
    public short getSubstreEntry(ColumnAST col, ExtractBaseAST rhs, short start, short length) {
        LogicTableF1 f1 = (LogicTableF1) getAssignmentEntry(col, rhs);
        LogicTableArg arg = f1.getArg();
        //This is different in that we need to get the original string value and change it?
        //arg.setStartPosition(start);
        short fieldlen = arg.getFieldLength();
        if(length < fieldlen) { 
            String val = ((StringAtomAST)rhs).getValue();
            arg.setValue(val.substring(start, start + length));
            arg.setValueLength(length);
            ltEmitter.addToLogicTable((LTRecord)f1);
        } else {
            //Error 
        }
        return length;
    }

}
