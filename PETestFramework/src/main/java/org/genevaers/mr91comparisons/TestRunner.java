package org.genevaers.mr91comparisons;

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


import com.google.common.flogger.FluentLogger;

public class TestRunner {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	public static void main(String[] args) {
		int exitCode = 1; //not good
    	Tester tester = new Tester();
    	boolean testsRan = tester.processMR91Inputs();
		if (testsRan) {
            TestReporter report = new TestReporter();
            try {
				report.generate();
	    		if(report.allPassed()) {
	    			logger.atInfo().log("All tests were successful");
	    			exitCode = 0;
	    		} else {
	    			logger.atSevere().log("Not all tests passed");
	    		}
			} catch (Exception e) {
				logger.atSevere().log("Exception occured in generating test reports: \n%s", e.getMessage());
			}
		}
		System.exit(exitCode);
    }
}
