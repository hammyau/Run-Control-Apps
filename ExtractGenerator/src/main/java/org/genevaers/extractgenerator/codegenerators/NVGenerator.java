package org.genevaers.extractgenerator.codegenerators;

import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.ltfile.LogicTableNV;
import org.genevaers.genevaio.ltfile.LogicTableRE;
import org.genevaers.repository.Repository;

import com.google.common.flogger.FluentLogger;

public class NVGenerator implements ExtractRecordGenerator {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    int outputLength;
    int lrLength;

    @Override
    public ExtractorEntry processRecord(LTRecord lt)  {
        LogicTableNV nv = (LogicTableNV)lt;
        logger.atInfo().log("NV  dt area %d", nv.getDtAreaLen());
        lrLength = Repository.getLRLength(nv.getSourceLrId());
        outputLength = nv.getDtAreaLen();
        return new ExtractorEntry(String.format("//Output length %d",outputLength ));
    }

    public int getOutputLength() {
        return outputLength;
    }

    public int getLrLength() {
        return lrLength;
    }

}
