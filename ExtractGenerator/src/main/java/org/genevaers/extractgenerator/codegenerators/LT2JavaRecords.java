package org.genevaers.extractgenerator.codegenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.genevaers.genevaio.ltfile.LTRecord;
import com.google.common.flogger.FluentLogger;

public class LT2JavaRecords {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static List<ExtractorEntry> exrecs = new ArrayList<>();
    private static List<String> inputDDnames = new ArrayList<>();
    private static int outputLength;
    private static int lrLength;
    private static Stack<Integer> gotos = new Stack<>();
    private static int endScopeRow;

    public static ExtractorEntry processRecord(LTRecord lt) {
        String fc = lt.getFunctionCode();
        switch(fc) {
            case "RENX":
                RENXGenerator reg = new RENXGenerator();
                reg.processRecord(lt);
                inputDDnames.addAll(reg.getInputs());
                break;
            case "NV":
                NVGenerator nv = new NVGenerator();
                exrecs.add(nv.processRecord(lt));
                outputLength = nv.getOutputLength();
                lrLength =  nv.getLrLength();
                break;
            case "DTC":
                DTCGenerator dtc = new DTCGenerator();
                exrecs.add(dtc.processRecord(lt));
                break;
            case "DTE":
                DTEGenerator dte = new DTEGenerator();
                exrecs.add(dte.processRecord(lt));
                break;
            case "CFEC":
                CFECGenerator cfec = new CFECGenerator();
                exrecs.add(cfec.processRecord(lt));
                gotos.push(cfec.getFalseRow());
                break;
            case "GOTO":
                GOTOGenerator gotofc = new GOTOGenerator();
                if(gotos.peek() == lt.getRowNbr()+1) {
                    gotofc.generateElse();
                    gotos.pop();
                }
                exrecs.add(gotofc.processRecord(lt));
                endScopeRow = gotofc.getEndScopeRow();
                break;
            case "WRDT":
                WRDTGenerator wrdt = new WRDTGenerator();
                wrdt.setOutputLength(outputLength);
                exrecs.add(wrdt.processRecord(lt));
                break;
            case "ES":
                ESGenerator es = new ESGenerator();
                exrecs.add(es.processRecord(lt));
                break;
            case "EN":
                ENGenerator en = new ENGenerator();
                exrecs.add(en.processRecord(lt));
                break;
            default:
                logger.atInfo().log("%s not handled", fc);
            break;
        }
        if(lt.getRowNbr() > 0 && lt.getRowNbr() == endScopeRow) {
            exrecs.add(new ExtractorEntry("}"));  //will need a stack here? And manage indent
        }
        return null;
    }

    public static List<ExtractorEntry> getExrecs() {
        return exrecs;
    }

    public static List<String> getInputDDnames() {
        return inputDDnames;
    }

    public static int getOutputLength() {
        return outputLength;
    }

    public static int getLrLength() {
        return lrLength;
    }

}