package org.genevaers.extractgenerator.codegenerators;

import java.io.FileWriter;
import java.io.IOException;

import org.genevaers.genevaio.ltfile.LTRecord;
import org.genevaers.genevaio.recordreader.RecordFileWriter;

import com.google.common.flogger.FluentLogger;

public class ExtractorRecord implements Extractor {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public ExtractorEntry processRecord(LTRecord lt) {
        String fc = lt.getFunctionCode();
        switch(fc) {
            case "RENX":
                RENXGenerator reg = new RENXGenerator();
                return reg.processRecord(lt);
            default:
                logger.atInfo().log("%s not hamdled", fc);
            break;
        }
        return null;
    }

}