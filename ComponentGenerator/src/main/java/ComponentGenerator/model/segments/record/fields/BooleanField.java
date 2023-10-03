package ComponentGenerator.model.segments.record.fields;

/*
 * Copyright Contributors to the GenevaERS Project. SPDX-License-Identifier: Apache-2.0 (c) Copyright IBM Corporation 2008
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


import org.apache.commons.lang3.StringUtils;

import ComponentGenerator.model.NameUtils;

public class BooleanField extends Field {

    private static final String TYPE = "boolean";

    @Override
    public String getImportString() {
        return null;
    }

    @Override
    public String getFieldEntry() {
        String entry = defaultFieldEntryForType(TYPE);
        if(getComponentField().equals("none")) { 
            entry += " = false";
        } 
        entry += ";";
        return entry;
    }

    @Override
    public String getGetAndSetEntry() {
        StringBuilder gasEntry = new StringBuilder();
        String ccName = NameUtils.getCamelCaseName(name, false);
        String ucName = NameUtils.getCamelCaseName(name, true);
        gasEntry.append("    public " + TYPE + " is" + ucName + "() {\n");
        gasEntry.append("        return " + ccName + ";\n    }\n\n");
        gasEntry.append("    public void set" + ucName + "(" + TYPE + " " + ccName + ") {\n");
        gasEntry.append("        this." + ccName + " = " + ccName + ";\n    }\n");
        return gasEntry.toString();
    }

    @Override
    public String getReadEntry() {
        StringBuilder readEntry = new StringBuilder();
        String ccName = NameUtils.getCamelCaseName(name, false);
        String uccName = NameUtils.getCamelCaseName(name, true);
        readEntry.append(DBLINDENT + "byte " + ccName + "_b = rec.bytes.get();\n");
        readEntry.append(DBLINDENT + "if (" + ccName + "_b == 1) {\n");
        readEntry.append(DBLINDENT + "    set" + uccName + "(true);\n");
        readEntry.append(DBLINDENT + "} else {\n");
        readEntry.append(DBLINDENT + "    set" + uccName + "(false);\n");
        readEntry.append(DBLINDENT + "}");
        return readEntry.toString();
    }

    @Override
    public String getCsvEntry() {
        StringBuilder csvEntry = new StringBuilder();
        String ccName = NameUtils.getCamelCaseName(name, false);
        csvEntry.append(DBLINDENT + "if (" + ccName + ") {\n");
        csvEntry.append(DBLINDENT + "    fw.write(\"True\");\n");
        csvEntry.append(DBLINDENT + "} else {\n");
        csvEntry.append(DBLINDENT + "    fw.write(\"False\");\n");
        csvEntry.append(DBLINDENT + "}\n");
        csvEntry.append(DBLINDENT + "fw.write(\",\");");
        return csvEntry.toString();
    }

    @Override
    public String getCsvHeaderEntry() {
        return defaultCsvHeaderEntry();
    }

    @Override
    public String getFillTheWriteBufferEntry() {
        StringBuilder fillEntry = new StringBuilder();
        fillEntry.append(DBLINDENT + "if (" + NameUtils.getCamelCaseName(name, false) + " == true) {\n");
        fillEntry.append(DBLINDENT + "    byte b = 1;\n");
        fillEntry.append(DBLINDENT + "    buffer.bytes.put(b);\n");
        fillEntry.append(DBLINDENT + "} else {\n");
        fillEntry.append(DBLINDENT + "    byte b = 0;\n");
        fillEntry.append(DBLINDENT + "    buffer.bytes.put(b);\n");
        fillEntry.append(DBLINDENT + "}");
        return fillEntry.toString();
    }

    @Override
    public String getFillFromComponentEntry() {
        String entry = null;
        if (!getComponentField().equals("none")) {
            String noPrefixName = name.replace("prefix.", "");
            String uccName = NameUtils.getCamelCaseName(noPrefixName, true);
            entry = DBLINDENT + "set" + uccName + "(component.is" + StringUtils.capitalize(componentField) + "());";
        }
        return entry;
    }

    @Override
    public String getDsectType() {
        return "C";
    }

    @Override
    public String getFieldNodeEntry() {
        StringBuilder fieldNodeEntry = new StringBuilder();
        String ccName = NameUtils.getCamelCaseName(name, false);
        fieldNodeEntry.append(DBLINDENT + "String " + ccName + "val;\n");
        fieldNodeEntry.append(DBLINDENT + "if (" + ccName + ") {\n");
        fieldNodeEntry.append(DBLINDENT + "     "+ ccName + "val = \"True\";\n");
        fieldNodeEntry.append(DBLINDENT + "} else {\n");
        fieldNodeEntry.append(DBLINDENT + "     "+ ccName + "val = \"False\";\n");
        fieldNodeEntry.append(DBLINDENT + "}\n");
        fieldNodeEntry.append(DBLINDENT + "rn.add(new StringFieldNode(\"" + NameUtils.getCamelCaseName(name, false) + "\", "+ ccName + "val ), compare);");  
        return fieldNodeEntry.toString();
    }

}
