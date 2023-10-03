package org.genevaers.genevaio.fieldnodes;

public class NumericFieldNode extends FieldNodeBase {

    private int value;
    private int diffValue;

    public NumericFieldNode(String name, int val) {
        setName(name);
        value = val;
        type = FieldNodeBase.FieldNodeType.NUMBERFIELD;
        state = ComparisonState.ORIGINAL;
    }

    public int getValue() {
        return value;
    }

    public String getValueString() {
        if(state == ComparisonState.DIFF) {
            return value + " -> " + diffValue;
        }
        return Integer.toString(value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(int diffValue) {
        this.diffValue = diffValue;
    }

    @Override
    public boolean compareTo(FieldNodeBase rn) {
        boolean result = true;
        if(value == ((NumericFieldNode)rn).getValue()) {
            state = ComparisonState.INSTANCE;
        } else {
            state = ComparisonState.DIFF;
            diffValue = ((NumericFieldNode)rn).getValue();
            result = false;
        }
        return result;
    }
}
