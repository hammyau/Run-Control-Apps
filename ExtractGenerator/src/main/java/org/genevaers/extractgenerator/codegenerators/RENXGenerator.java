package org.genevaers.extractgenerator.codegenerators;

import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.ltfile.LogicTableRE;
import com.google.common.flogger.FluentLogger;

public class RENXGenerator implements Extractor{
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public ExtractorEntry processRecord(LTRecord lt)  {
        LogicTableRE renx = (LogicTableRE)lt;
        logger.atInfo().log("RENX read from file id %d", renx.getFileId());
        return new ExtractorEntry(String.format("RENX read from file id %d\n", renx.getFileId()));
    }
    
}
