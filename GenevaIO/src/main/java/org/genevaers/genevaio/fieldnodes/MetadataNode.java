package org.genevaers.genevaio.fieldnodes;

public class MetadataNode extends FieldNodeBase{

    private String source1;
    private String source2;

    public MetadataNode() {
        type = FieldNodeBase.FieldNodeType.METADATA;
        state = ComparisonState.ORIGINAL;
    }

    public void setSource1(String source1) {
        this.source1 = source1;
    }

    public void setSource2(String source2) {
        this.source2 = source2;
    }

    public String getSource1() {
        return source1;
    }

    public String getSource2() {
        return source2;
    }

}
