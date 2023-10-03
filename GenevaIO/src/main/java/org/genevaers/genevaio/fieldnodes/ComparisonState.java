package org.genevaers.genevaio.fieldnodes;

public enum ComparisonState {
    ORIGINAL,
    NEW,
    DIFF,
    CHANGED, // means there is a DIFF below
    INSTANCE
}
