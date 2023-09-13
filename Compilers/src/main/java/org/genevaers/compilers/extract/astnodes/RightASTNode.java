package org.genevaers.compilers.extract.astnodes;

import org.genevaers.genevaio.ltfile.LTFileObject;

public class RightASTNode extends StringFunctionASTNode implements Assignable{


    public RightASTNode() {
        type = ASTFactory.Type.RIGHT;
    }

    @Override
    public LTFileObject getAssignmentEntry(ColumnAST col, ExtractBaseAST rhs) {
       if(getNumberOfChildren() == 1) {
            Concatable cc =  (Concatable) getChildIterator().next();
            cc.getRightEntry(col, (ExtractBaseAST) cc, Short.valueOf(getLength()));
        }
        return null;
    }

}
