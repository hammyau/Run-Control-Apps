package org.genevaers.extractgenerator.codegenerators;

import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.ltfile.LogicTableArg;
import org.genevaers.genevaio.ltfile.LogicTableF0;

import com.google.common.flogger.FluentLogger;

public class GOTOGenerator implements ExtractRecordGenerator{
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private boolean generateElse;
    private int endScopeRow;

    @Override
    public ExtractorEntry processRecord(LTRecord lt) {
        LogicTableF0 gotofc = (LogicTableF0)lt;
        gotofc.getGotoRow1();
        logger.atInfo().log("GOTO %d ", gotofc.getGotoRow1());
        String exec = "        }";
        if(generateElse) {
            exec += " else {";
        }
        endScopeRow = gotofc.getGotoRow1();
        return new ExtractorEntry(exec);
    }

    public void generateElse() {
        generateElse = true;
    }

    public int getEndScopeRow() {
        return endScopeRow;
    }

}
