package org.genevaers.extractgenerator.codegenerators;

import org.genevaers.genevaio.ltfile.LTRecord;

public interface ExtractRecordGenerator {

    public ExtractorEntry processRecord(LTRecord lt);

}