package org.genevaers.genevaio.fieldnodes;

public class RecordNode extends FieldNodeBase{

    public RecordNode() {
        type = FieldNodeBase.FieldNodeType.RECORD;
        state = ComparisonState.ORIGINAL;
    }

}
