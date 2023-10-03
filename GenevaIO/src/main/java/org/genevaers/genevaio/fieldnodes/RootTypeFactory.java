package org.genevaers.genevaio.fieldnodes;

import org.genevaers.genevaio.vdpfile.record.VDPRecord;

public class RootTypeFactory {
    public static RecordNode getRecordNodeForType(short recType) {
        RecordNode typeNode = new RecordNode();
        switch (recType) {
            case VDPRecord.VDP_GENERATION:
                typeNode.setName("Generation");
                break;
            case VDPRecord.VDP_FORMAT_VIEWS:
                typeNode.setName("Format Views");
                break;
            case VDPRecord.VDP_CONTROL:
                typeNode.setName("Control Records");
                break;
            case VDPRecord.VDP_PHYSICAL_FILE:
                typeNode.setName("Physical Files");
                break;
            case VDPRecord.VDP_EXIT:
                typeNode.setName("Exits");
                break;
            case VDPRecord.VDP_LOGICAL_RECORD:
                typeNode.setName("Logical Records");
                break;
            case VDPRecord.VDP_LRFIELD:
                typeNode.setName("LR Fields");
                break;
            case VDPRecord.VDP_INDEX:
                typeNode.setName("LR Indexes");
                break;
            case VDPRecord.VDP_LOOKUP:
            case VDPRecord.VDP_LOOKUP_OLD:
                typeNode.setName("Lookup Paths");
                break;
            case VDPRecord.VDP_LOOKUP_TARGET_SET:
                typeNode.setName("Lookup Target Set");
                break;
            case VDPRecord.VDP_LOOKUP_GENMAP:
                typeNode.setName("Lookup Generation Map");
                break;
            case VDPRecord.VDP_EXTRACT_OUTPUT_FILE:
                typeNode.setName("Extract Output File");
                break;
            case VDPRecord.VDP_EXTRACT_RECORD_FILE:
                typeNode.setName("Extract Record File");
                break;
            case VDPRecord.VDP_VIEW:
                typeNode.setName("View Definition");
                break;
            case VDPRecord.VDP_FORMAT_OUTPUT_FILE:
                typeNode.setName("View Output File");
                break;
            case VDPRecord.VDP_EXTRACT_TARGET_SET:
                typeNode.setName("Extract Target Set");
                break;
            case VDPRecord.VDP_COLUMN:
                typeNode.setName("Columns");
                break;
            case VDPRecord.VDP_SORT_KEY:
                typeNode.setName("Sort Keys");
                break;
            case VDPRecord.VDP_VIEW_SOURCE:
                typeNode.setName("Sources");
                break;
            case VDPRecord.VDP_EXTRACT_FILTER:
                typeNode.setName("Extract Filter");
                break;
            case VDPRecord.VDP_COLUMN_SOURCE:
                typeNode.setName("Column Sources");
                break;
            case VDPRecord.VDP_COLUMN_LOGIC:
                typeNode.setName("Column Logic");
                break;
            case VDPRecord.VDP_OUTPUT_LOGIC:
                typeNode.setName("Output Logic");
                break;
            case VDPRecord.VDP_FORMAT_FILTER_LOGIC:
                typeNode.setName("Format Filter");
                break;
            case VDPRecord.VDP_COLUMN_CALCULATION:
                typeNode.setName("Column Calculation");
                break;
            case VDPRecord.VDP_FORMAT_FILTER_STACK:
                typeNode.setName("Format Filter Stack");
                break;
            case VDPRecord.VDP_COLUMN_CALCULATION_LT:
                typeNode.setName("Column Calculation Stack");
                break;
            case VDPRecord.VDP_HEADER:
                typeNode.setName("Header");
                break;
            case VDPRecord.VDP_FOOTER:
                typeNode.setName("Footer");
                break;

            case VDPRecord.VDP_FORMAT_FILTER_OLD:
            case VDPRecord.VDP_COLUMN_CALCULATION_OLD:
            case VDPRecord.VDP_EXTRACT_FILTER_OLD:
            case VDPRecord.VDP_COLUMN_LOGIC_OLD:
            case VDPRecord.VDP_COLUMN_LOGIC_LT:
            default:
        }
        return typeNode;
    }
}
