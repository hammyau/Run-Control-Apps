package org.genevaers.runcontrolanalyser;

/*
 * Copyright Contributors to the GenevaERS Project. SPDX-License-Identifier: Apache-2.0 (c) Copyright IBM Corporation 2008
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


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.genevaers.runcontrolanalyser.menu.RCAGenerationData;
import org.genevaers.utilities.GenevaLog;

import com.google.common.flogger.FluentLogger;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CommandLineHandler {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	static AnalyserDriver flow = new AnalyserDriver();
	private static boolean showHelp = true;
	private static String username;
	private static String password;
	private static String servername;
	private static String dataset;

	private static Path dataStore;


	public static void main(String[] args) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException, InterruptedException {
		Options options = buildCommandLineOptions();
		CommandLineParser parser = new DefaultParser();
		try {
			//initLogger();
			GenevaLog.formatConsoleLogger(CommandLineHandler.class.getName(), Level.FINE);
			CommandLine line = parser.parse( options, args );
			logJavaDetails();
			processTheCommandLine(line);
			//showHelpIfNeeded(options, showHelp);

		}
		catch( ParseException exp ) {
			logger.atSevere().log("Parsing failed.  Reason: %s", exp.getMessage() );
		}
        GenevaLog.closeLogger(CommandLineHandler.class.getName());
	}

	private static void logJavaDetails() {
		logger.atInfo().log("Java Vendor %s", System.getProperty("java.vendor"));
		logger.atInfo().log("Java Version %s", System.getProperty("java.version"));
	}

	// A default command line inteface should be simply expect run control files in the local directory
	// and write the generated outputs to a subdirectory
	private static void processTheCommandLine(CommandLine line)  {
		String locroot = System.getProperty("user.dir");
    	//locroot = locroot.replaceAll("^[Cc]:", "");
    	locroot = locroot.replace("\\", "/");
    	Path root = Paths.get(locroot);
		if(line.hasOption("menu")) {
			flow.makeRunControlAnalyserDataStore(null);
			RCAGenerationData.setFlow(flow);
			RCAMenu.generate();
		} else if(line.hasOption("x")) {
			flow.generateXltPrint(root);
		} else if(line.hasOption("j")) {
			flow.generateJltPrint(root);
		} else if(line.hasOption("c")) {
			flow.aggregateLtCoverage();
		} else if(line.hasOption("d")) {
			generateDiffReport(root);
		} else if(line.hasOption("help")) {
			showHelpIfNeeded(buildCommandLineOptions(), true);
		} else {
			flow.makeRunControlAnalyserDataStore(root);
			root.resolve("rca").toFile().mkdirs();
			flow.generateXltPrint(root);
			flow.generateJltPrint(root);
			flow.writeCoverageResults(root);
			generateFlow();
		}

	}

	private static void generateDiffReport(Path root) {
		//Confirm expected subdirs
		logger.atInfo().log("Generate Diff Report");
		try {
			flow.diffReport(root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showHelpIfNeeded(Options options, boolean showHelp) {
		if(showHelp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "runcontrolanalyser", options );
		}
	}

	private static Options buildCommandLineOptions() {
		Options options = new Options();

		Option help = new Option( "help", "print this message" );
		Option menu = new Option( "menu", "run-menu",  false, "run from menu");
		Option xltPrint = new Option( "x", "xltprint",  false, "generate xltprint");
		Option jltPrint = new Option( "j", "jltprint",  false, "generate jltprint");
		Option ltcover = new Option( "c", "ltcoverage",  false, "logic table coverage");
		//Diff report implies two subdirectories RC1 and RC2
		//Think about database and XML base comparisons later. May be that RCG needs to have been run?
		Option diffReport = new Option( "d", "diff",  false, "generate diff report");
		
		options.addOption( help );
		options.addOption(menu);
		options.addOption(xltPrint);
		options.addOption(jltPrint);		
		options.addOption(ltcover);
		options.addOption(diffReport);

		return options;
	}

	private static void initLogger() {
        GenevaLog.initLogger(CommandLineHandler.class.getName(), "gersfa", Level.FINE);
	}

	public static String readVersion() {
		String version = "unknown";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
			properties.load(resourceStream);
            version = properties.getProperty("build.version") + " (" + properties.getProperty("build.timestamp") + ")";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return version;
	}

	private static void generateFlow() {
        try {
            flow.setTargetDirectory("rca");
            flow.generateFlowDataFrom(".", 
             true,  //default to generate csv
            true,
            ""
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
