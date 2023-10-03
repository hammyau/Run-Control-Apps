package org.genevaers.runcontrolanalyser;
/*
 * Copyright Contributors to the GenevaERS Project. SPDX-License-Identifier: Apache-2.0 (c) Copyright IBM Corporation 2008.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import com.google.common.flogger.FluentLogger;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.genevaers.genevaio.dots.LookupGenerationDotWriter;
import org.genevaers.genevaio.fieldnodes.MetadataNode;
import org.genevaers.genevaio.fieldnodes.RecordNode;
import org.genevaers.genevaio.html.VDPHTMLWriter;
import org.genevaers.genevaio.ltfile.LogicTable;
import org.genevaers.genevaio.ltfile.XLTFileReader;
import org.genevaers.genevaio.ltfile.writer.LTCSVWriter;
import org.genevaers.genevaio.vdpfile.VDPFileReader;
import org.genevaers.genevaio.vdpfile.VDPManagementRecords;
import org.genevaers.repository.Repository;
import org.genevaers.utilities.CommandRunner;
import org.genevaers.visualisation.GraphVizRunner;

public class RunControlAnalyser {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	private String htmlFileName;
	private LogicTable xlt;
	private LogicTable jlt;
	private Path trg;
	private VDPManagementRecords vmrs;

	private FlowGenerator flowGen;

	public RunControlAnalyser() {
		trg = Paths.get("RunControls");
	}

	public void readVDP(Path vdpPath, boolean withCSV, MetadataNode recordsRoot, boolean compare) throws Exception {
		logger.atInfo().log("Read VDP %s csv flag %s", vdpPath, Boolean.toString(withCSV));
		if(vdpPath.toFile().exists()) {
			VDPFileReader vdpr = new VDPFileReader();
			vdpr.setCsvPath(trg);
			vdpr.setRecordsRoot(recordsRoot);
			vdpr.setCompare(compare);
			vdpr.open(vdpPath);
			vdpr.addToRepsitory(withCSV);
			vmrs = vdpr.getViewManagementRecords();
		} else {
			logger.atSevere().log("VDP %s not found");
		}
		
	}

	public void readXLT(Path xltPath, boolean withCSV, RecordNode recordsRoot, boolean compare) {
		logger.atInfo().log("Read XLT %s csv flag %s", xltPath, Boolean.toString(withCSV));
		XLTFileReader xltr = new XLTFileReader();
		xltr.setCompare(compare);
		xltr.setRecordsRoot(recordsRoot);
		xltr.open(xltPath.toString());
		xlt = xltr.makeLT();
		if(withCSV) {
			try {
				writeXLTCSV("XLT");
			} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void readJLT(Path jltPath, boolean withCSV, RecordNode recordsRoot, boolean compare) {
		logger.atInfo().log("Read JLT %s csv flag %s", jltPath, Boolean.toString(withCSV));
		if(jltPath.toFile().exists()) {
			XLTFileReader jltr = new XLTFileReader();
			jltr.setCompare(compare);
			jltr.setRecordsRoot(recordsRoot);
			jltr.open(jltPath.toString());
			jlt = jltr.makeLT();
			if(withCSV) {
				try {
					writeJLTCSV("JLT");
				} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			logger.atInfo().log("No JLT found");
		}
	}

	public void writeHTML(String joinsFilter) throws IOException {
		/*
		 * We want to generate the Dot nodes for
		 * 	lookup diagrams
		 *  View diagrams
		 *  Flow diagrams
		 *  Then the html Writer can do its thing and link to the products of what we generate
		 *  It does not generate them itself
		 *  Single responsibility and all that
		 */
		
		writeViewDiagrams(xlt, joinsFilter);
		//This must follow the above which builds the flow.dot file
		writeFlowAndLookupGenerationDiagrams(jlt);
		
		VDPHTMLWriter htmlWriter= new VDPHTMLWriter();
		htmlWriter.setCurrentWorkingDirectory(trg);
		htmlWriter.setFileName(htmlFileName);
		htmlWriter.write(vmrs);
		htmlWriter.close();
	}

	private void writeViewDiagrams(LogicTable xlt, String joinsFilter) throws IOException {
		flowGen = new FlowGenerator(vmrs, xlt);
		flowGen.setCurrentWorkingDirectory(trg);
		flowGen.setDetailJoins(joinsFilter);
		flowGen.writeDotFilesFromLT("flow.dot");
		Path vws = trg.resolve("views");
		vws.toFile().mkdirs();
		convertDotsToSVGs(vws);
	}

	private void writeFlowAndLookupGenerationDiagrams(LogicTable jlt) throws IOException {
		if(Repository.getLookups().size() > 0 && jlt != null) {
			LookupGenerationDotWriter lgw = new LookupGenerationDotWriter(jlt);
			lgw.writeREDGenerations(trg.resolve("views"));
			flowGen.setLogicTable(jlt);
			flowGen.clearView2fileMap();
			flowGen.writeDotFilesFromLT("joins.dot");
		}
		convertDotsToSVGs(trg);
	}
	
	private void convertDotsToSVGs(Path vdpPath) {
		//Iterate through all of the dot files and convert to svg
		//logger.info("Processing All dot files in: " + vdpPath.toString());
		// want to find all of the xml files under the spec
		// use the Apache beast
		WildcardFileFilter fileFilter = new WildcardFileFilter("*.dot");
		Collection<File> dotFiles = FileUtils.listFiles(vdpPath.toFile(), fileFilter, FalseFileFilter.FALSE);
		
		for(File d : dotFiles) {
	        //Need to find an iterate for these ... but
	        GraphVizRunner gvr = new GraphVizRunner();
	        gvr.processDot(d);
		}
	}


	public void setHTMLFileName(String name) {
		htmlFileName = name;
	}

	public void writeXLTCSV(String xltName) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		Path output = trg.resolve(xltName + ".csv");
		LTCSVWriter csvw = new LTCSVWriter();
		csvw.write(xlt, output);
		csvw.close();
	}

	public void writeJLTCSV(String jltName) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		Path output = trg.resolve(jltName + ".csv");
		LTCSVWriter csvw = new LTCSVWriter();
		csvw.write(jlt, output);
		csvw.close();
	}

	public void setTargetDirectory(Path trgPath) {
		trg = trgPath;
	}

	public void generateFlowDataFrom(String baseFileName, boolean withCSV, boolean noBrowse, String joinsFilter) throws Exception {
		File baseDir = new File(baseFileName);
		if(baseDir.isDirectory()) {
			if(!baseFileName.endsWith("/")) {
				baseFileName += "/";
			}
			String vdp = baseFileName + "VDP";
			String xlt = baseFileName + "XLT";
			String jlt = baseFileName + "JLT";
			Path vdpName = Paths.get(vdp);
			Path xltName = Paths.get(xlt);
			Path jltName = Paths.get(jlt);
			htmlFileName = "gersrca.html";

			logger.atInfo().log("Read VDP File %s", vdpName);
			logger.atInfo().log("Read XLT File %s", xltName);
			logger.atInfo().log("Read JLT File %s", jltName);
			logger.atInfo().log("Write to %s", htmlFileName);

			readVDP(vdpName, withCSV, null, false);
			readXLT(xltName, withCSV, null, false);
			readJLT(jltName, withCSV, null, false);
			writeHTML(joinsFilter);
			
			if(noBrowse == false) {
				openHTML();
			}
		} else {
			logger.atSevere().log("Supplied run control argument %s is not a directory", baseFileName);
		}
	}
	
	public void openHTML() throws IOException, InterruptedException {
		Path showme = trg.resolve(htmlFileName);
		String os = System.getProperty("os.name");
		CommandRunner cmdRunner = new CommandRunner();
		if(os.startsWith("Windows")) {
			cmdRunner.run("cmd /C " + showme.toString(), showme.getParent().toFile());
		} else {
			cmdRunner.run("open /Applications/Firefox.app/ " + showme.toString(), showme.getParent().toFile());
		}
	}

	Path getCurrentWorkiingDirectory() {
		return trg;
	}

	public void openAnalysis(String rcSet) throws IOException, InterruptedException {
		Path showme = trg.resolve(rcSet);
		CommandRunner cmdRunner = new CommandRunner();
		cmdRunner.run("cmd /C " + showme.toString(), showme.getParent().toFile());
	}
}
