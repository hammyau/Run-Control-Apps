package org.genevaers.compilers.extract.astnodes;

import org.genevaers.genevaio.ltfile.LTFileObject;

public class SubStringASTNode extends StringFunctionASTNode implements Assignable{

    String startOffest = "0";

    public SubStringASTNode() {
        type = ASTFactory.Type.SUBSTR;
    }

    @Override
    public LTFileObject getAssignmentEntry(ColumnAST col, ExtractBaseAST rhs) {
        if(getNumberOfChildren() == 1) {
            Concatable cc =  (Concatable) getChildIterator().next();
            cc.getSubstreEntry(col, (ExtractBaseAST) cc, Short.valueOf(getStartOffest()), Short.valueOf(getLength()));
        }
        return null;
    }

    public String getStartOffest() {
        return startOffest;
    }

    public void setStartOffest(String startOffest) {
        this.startOffest = startOffest;
    }
 }
