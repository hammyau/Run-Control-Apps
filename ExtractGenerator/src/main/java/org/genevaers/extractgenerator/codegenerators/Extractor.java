package org.genevaers.extractgenerator.codegenerators;

import java.io.FileWriter;
import java.io.IOException;

import org.genevaers.genevaio.ltfile.LTRecord;

public interface Extractor {

    public ExtractorEntry processRecord(LTRecord lt);

}