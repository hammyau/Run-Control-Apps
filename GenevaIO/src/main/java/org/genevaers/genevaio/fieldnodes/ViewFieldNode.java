package org.genevaers.genevaio.fieldnodes;

public class ViewFieldNode extends FieldNodeBase{

    private Integer typeNumber;

    public ViewFieldNode() {
        type = FieldNodeBase.FieldNodeType.VIEW;
        state = ComparisonState.ORIGINAL;
    }

    public void setTypeNumber(Integer typeNumber) {
        this.typeNumber = typeNumber;
    }

    public Integer getTypeNumber() {
        return typeNumber;
    }

}
