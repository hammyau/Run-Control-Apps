package org.genevaers.compilers.extract.astnodes;

import org.genevaers.genevaio.ltfile.LTFileObject;

public class LeftASTNode extends StringFunctionASTNode implements Assignable{

    public LeftASTNode() {
        type = ASTFactory.Type.LEFT;
    }

    @Override
    public LTFileObject getAssignmentEntry(ColumnAST col, ExtractBaseAST rhs) {
        if(getNumberOfChildren() == 1) {
            Concatable cc =  (Concatable) getChildIterator().next();
            cc.getLeftEntry(col, (ExtractBaseAST) cc, Short.valueOf(getLength()));
        }
        return null;
    }

}
