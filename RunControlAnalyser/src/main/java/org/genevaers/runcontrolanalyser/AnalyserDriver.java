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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.genevaers.genevaio.fieldnodes.MetadataNode;
import org.genevaers.genevaio.fieldnodes.RecordNode;
import org.genevaers.genevaio.fieldnodes.Records2Dot;
import org.genevaers.genevaio.html.LTRecordsHTMLWriter;
import org.genevaers.genevaio.html.VDPRecordsHTMLWriter;
import org.genevaers.genevaio.ltfile.LTLogger;
import org.genevaers.genevaio.ltfile.LogicTable;
import org.genevaers.genevaio.ltfile.XLTFileReader;
import org.genevaers.runcontrolanalyser.ltcoverage.LTCoverageAnalyser;
import org.genevaers.utilities.CommandRunner;
import org.genevaers.utilities.FTPSession;

import com.google.common.flogger.FluentLogger;


public class AnalyserDriver {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	/**
	 *
	 */
	private static final String TO = "' to ";
	/**
	 *
	 */
	private static final String FTP_GET = "FTP Get ";
	private RunControlAnalyser fa = new RunControlAnalyser();
	private Object cwd;
	private Path dataStore;
	private LTCoverageAnalyser ltCoverageAnalyser = new LTCoverageAnalyser();

	private boolean jlt1Present;

	private boolean jlt2Present;

	public void ftpRunControlDatasets(String host, String dataset, String rc, String  user, String password) throws IOException {

		FTPSession session = new FTPSession(host);
		session.setUserAndPassword(user, password);
		session.connect();
		Path newcwd = Paths.get(rc);
		newcwd.toFile().mkdirs();
		String vdpDataset = dataset + ".VDP";
		String xltDataset = dataset + ".XLT";
		String jltDataset = dataset + ".JLT";
		Path vdpPath = Paths.get(rc).resolve("VDP");
		Path xltPath = Paths.get(rc).resolve("XLT");
		Path jltPath = Paths.get(rc).resolve("JLT");
		session.setRDW();
		session.setBinary();
		System.out.println(FTP_GET + vdpDataset + TO
				+ vdpPath.toString());
		session.getFile("'" + dataset + ".VDP" + "'", vdpPath.toFile());
		System.out.println(FTP_GET + xltDataset + TO
				+ xltPath.toString());
		session.getFile("'" + dataset + ".XLT" + "'", xltPath.toFile());
		System.out.println(FTP_GET + jltDataset + TO
				+ jltPath.toString());
		session.getFile("'" + dataset + ".JLT" + "'", jltPath.toFile());
		System.out.println("FTP transfers complete");
		session.disconnect();
	}

	public void generateFlowDataFrom(String baseFileName, boolean withCSV, boolean noBrowse, String joinsFilter) throws Exception {
		fa.generateFlowDataFrom(baseFileName, withCSV, noBrowse, joinsFilter);
	}

	public void setTargetDirectory(String trg) {
		Path trgPath = dataStore.resolve(trg);
		if(trgPath.toFile().exists() == false) {
			trgPath.toFile().mkdirs();
		}
		fa.setTargetDirectory(trgPath);
	}

	public void listRunControlDataSets() {
		WildcardFileFilter fileFilter = new WildcardFileFilter("*.VDP");
		Collection<File> rcFiles = FileUtils.listFiles(fa.getCurrentWorkiingDirectory().toFile(), fileFilter, DirectoryFileFilter.INSTANCE);
		
		for(File rc : rcFiles) {
			System.out.println(rc.toString());
		}
	}

	public void openDataStore() {
		try {
			//On a mac this will be different
			Runtime.getRuntime().exec("explorer.exe /root," + dataStore.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void makeRunControlAnalyserDataStore(Path root) {
		if(root == null) {
			String home = System.getProperty("user.home");
			//locroot = locroot.replaceAll("^[Cc]:", "");
			home = home.replace("\\", "/");
			Path homePath = Paths.get(home);
			dataStore = homePath.resolve(".gersflows");
		} else {
			dataStore = root;
		}
		if(dataStore.toFile().exists() == false) {
			dataStore.toFile().mkdirs();
		}
	}

	public Path getDataStore() {
		return dataStore;
	}

	public void openHtmlFor(String rcSet)  {
		Path showme = dataStore.resolve(rcSet).resolve("gersrca.html");
		CommandRunner cmdRunner = new CommandRunner();
		try {
			cmdRunner.run("cmd /C " + showme.toString(), showme.getParent().toFile());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getRcSets() {
		return dataStore.toFile().list();
	}

	public void generateXltPrint(Path root) {
		XLTFileReader xltr = new XLTFileReader();
		Path xltp = root.resolve("XLT");
		xltr.open(xltp.toString());
		LogicTable xlt = xltr.makeLT();
        LTLogger.logRecords(xlt);
        LTLogger.writeRecordsTo(xlt, root.resolve("rca").resolve("xltrpt.txt"));
		collectCoverageDataFrom(xltp, xlt);
	}

    private void collectCoverageDataFrom(Path xltp, LogicTable xlt) {
		ltCoverageAnalyser.addDataFrom(xltp, xlt);
	}

	public void generateJltPrint(Path root) {
		XLTFileReader jltr = new XLTFileReader();
		Path jltp = root.resolve("JLT");
		if(jltp.toFile().exists()) {
			jltr.open(root.resolve("JLT").toString());
			
			LogicTable jlt = jltr.makeLT();
			LTLogger.logRecords(jlt);
			LTLogger.writeRecordsTo(jlt, root.resolve("rca").resolve("jltrpt.txt"));
			collectCoverageDataFrom(jltp, jlt);
		}
    }

	public void writeCoverageResults(Path root) {
		ltCoverageAnalyser.setName(root.getFileName());
		ltCoverageAnalyser.writeResults(root.resolve("rca"));
	}

	public void aggregateLtCoverage() {
		ltCoverageAnalyser.aggregateCoverage();
	}

    public void diffReport(Path root) throws Exception {
		//What kind of diff report?
		//Based on what data?
		// RCG case
		// Look for Run Control Files
		if(runControlFilesPresent(root)) {
			Path rc1 = root.resolve("RC1");
			Path rc2 = root.resolve("RC2");
			generateVDPDiffReport(root, rc1, rc2);
			generateXLTDiffReport(root, rc1, rc2);
			generateJLTDiffReport(root, rc1, rc2);
		}
    }

	private void generateJLTDiffReport(Path root, Path rc1, Path rc2) {
		if(jlt1Present) {
			RecordNode recordsRoot = new RecordNode();
			recordsRoot.setName("Root");
			fa.readXLT(rc1.resolve("JLT"), false, recordsRoot, false);
			logger.atInfo().log("JLT Tree built from %s", rc1.toString());
			Records2Dot.write(recordsRoot, root.resolve("JLT1records.gv"));
			fa.readXLT(rc2.resolve("JLT"), false, recordsRoot, true);
			logger.atInfo().log("JLT Tree added to from %s", rc2.toString());
			Records2Dot.write(recordsRoot, root.resolve("JLTrecords.gv"));
			LTRecordsHTMLWriter.writeFromRecordNodes(root, recordsRoot, "JLT.html");
		}
	}

	private void generateXLTDiffReport(Path root, Path rc1, Path rc2) {
		RecordNode recordsRoot = new RecordNode();
		recordsRoot.setName("Root");
		fa.readXLT(rc1.resolve("XLT"), false, recordsRoot, false);
		logger.atInfo().log("XLT Tree built from %s", rc1.toString());
		Records2Dot.write(recordsRoot, root.resolve("xlt1records.gv"));
		fa.readXLT(rc2.resolve("XLT"), false, recordsRoot, true);
		logger.atInfo().log("XLT Tree added to from %s", rc2.toString());
		Records2Dot.write(recordsRoot, root.resolve("xltrecords.gv"));
		LTRecordsHTMLWriter.writeFromRecordNodes(root, recordsRoot, "XLT.html");
	}

	private void generateVDPDiffReport(Path root, Path rc1, Path rc2) throws Exception {
		MetadataNode recordsRoot = new MetadataNode();
		recordsRoot.setName("Root");
		recordsRoot.setSource1(root.relativize(rc1.resolve("VDP")).toString());
		recordsRoot.setSource2(root.relativize(rc2.resolve("VDP")).toString());
		fa.readVDP(rc1.resolve("VDP"), false, recordsRoot, false);
		logger.atInfo().log("VDP Tree built from %s", rc1.toString());
		VDPRecordsHTMLWriter.writeFromRecordNodes(root, recordsRoot, "VDP1.html");
		fa.readVDP(rc2.resolve("VDP"), false, recordsRoot, true);
		logger.atInfo().log("VDP Tree added to from %s", rc2.toString());
		Records2Dot.write(recordsRoot, root.resolve("records.gv"));
		VDPRecordsHTMLWriter.writeFromRecordNodes(root, recordsRoot, "VDPDiff.html");
	}

	private boolean runControlFilesPresent(Path root) {
		boolean allPresent = false;
		Path rc1 = root.resolve("RC1");
		Path rc2 = root.resolve("RC2");
		if(rcFilesPresent(rc1) && rcFilesPresent(rc2)) {
			logger.atInfo().log("Run control files found");
			jlt1Present = checkJLTPresent(rc1, "RC1");
			jlt2Present = checkJLTPresent(rc1, "RC2");
			allPresent = true;
		} else {
			logger.atSevere().log("Not all run control files are present.\nNeed subdirectories RC1 and RC2 to have VDP, XLT amd JLT files.");
		}
		return allPresent;
	}

	private boolean checkJLTPresent(Path rc, String name) {
		boolean present = false;
		Path jltPath = rc.resolve("JLT");
			if(jltPath.toFile().exists()) {
				logger.atInfo().log("JLT present %s", name);
				present = true;
			} else {
				logger.atInfo().log("No JLT for %s", name);
			};
		return present;
	}

	private boolean rcFilesPresent(Path rc) {
		Path vdpPath = rc.resolve("VDP");
		Path xltPath = rc.resolve("XLT");
		return vdpPath.toFile().exists() &&	xltPath.toFile().exists();
	}

}
