package org.genevaers.extractgenerator.codegenerators;

import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.ltfile.LogicTableF1;

import com.google.common.flogger.FluentLogger;

public class JoinGenerator implements ExtractRecordGenerator{
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private int joinid;
    private int targLf;
    private int targLr;
    private String newid;

    @Override
    public ExtractorEntry processRecord(LTRecord lt) {
        LogicTableF1 join = (LogicTableF1)lt;
        joinid = join.getColumnId();
        targLf = join.getArg().getLogfileId();
        targLr = join.getArg().getLrId();
        newid = join.getArg().getValue().getPrintString();
        logger.atInfo().log("Join %d -> %s targ LF %d LR %d", joinid, newid, targLf, targLr);
       
        return new ExtractorEntry(String.format("//Join %d -> %s targ LF %d LR %d\n" +
"                Join jn = JoinsRepo.getJoin(1);\n" +
"        //Record count used for do again \n" +
"        FileRecord joinBuffer = jn.getBufferForRecord(numrecords);\n" +
"        if(joinBuffer == null && jn.updateRequired()) {\n", joinid, newid, targLf, targLr));
    }

    public int getJoinid() {
        return joinid;
    }
    
    public String getNewid() {
        return newid;
    }

    public int getTargLf() {
        return targLf;
    }

    public int getTargLr() {
        return targLr;
    }
 
}
