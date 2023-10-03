package org.genevaers.genevaio.fieldnodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FieldNodeBase {
    public enum FieldNodeType {
        RECORD("Record"), 
        STRINGFIELD("String"),
        NUMBERFIELD("Number"), 
        METADATA("Metadata"), 
        VIEW("View"), 
        ROOT("Root"), 
        RECORDPART("RecordPart")
         ;

        private String name;
        private FieldNodeType(String n) {
            name = n;
        }
        @Override
        public String toString() {
            return name;
        }

    }

    private String name;
    protected ComparisonState state;
    private FieldNodeBase parent;
    protected FieldNodeType type;
    private Map<String, FieldNodeBase> childrenByName = new TreeMap<>();
    private List<FieldNodeBase> children = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComparisonState getState() {
        return state;
    }

    public void setState(ComparisonState state) {
        this.state = state;
    }

    public FieldNodeBase getParent() {
        return parent;
    }

    public void setParent(FieldNodeBase parent) {
        this.parent = parent;
    }

    public FieldNodeType getFieldNodeType() {
        return type;
    }

    public List<FieldNodeBase> getChildren() {
        return children;
    }

    public void setChildren(List<FieldNodeBase> children) {
        this.children = children;
    }

    public FieldNodeBase add(FieldNodeBase rn, boolean compare) {
        FieldNodeBase useThidOne = rn;
        rn.setParent(this);
        if(compare) {
            FieldNodeBase originalNode = childrenByName.get(rn.getName());
            if(originalNode != null) {
                boolean result = originalNode.compareTo(rn);
                if(result == false) {
                    this.setState(ComparisonState.DIFF);
                }
                useThidOne = originalNode;
            } else {
                rn.setState(ComparisonState.NEW);
                children.add(rn);
                childrenByName.put(rn.getName(), rn);                
            }
        } else {
            children.add(rn);
            childrenByName.put(rn.getName(), rn);
        }
        return useThidOne;
    }

    public boolean compareTo(FieldNodeBase rn) {
        state = ComparisonState.INSTANCE;
        return true;
    }
}
