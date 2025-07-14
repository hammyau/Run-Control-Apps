package org.genevaers.genevaio.yamlreader;

import com.fasterxml.jackson.annotation.JsonIgnore;

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


public class LRIndexFieldTransfer  {
	/*
	 * Fields used for table LRINDEXFLD.
	 */
	// associationId is LRINDEXFLD
	// associatingComponentId is LRINDEXID
	// associatedComponentId is LRFIELDID
	@JsonIgnore
	private Integer environmentId;
	private Integer associationId;
	private Integer associatedComponentId;
	private Integer fldSeqNbr;
	private String associatedComponentName;
	private Integer associatingComponentId;
	private String associatingComponentName;

	public String getAssociatedComponentName() {
		return associatedComponentName;
	}

	public void setAssociatedComponentName(String associatedComponentName) {
		this.associatedComponentName = associatedComponentName;
	}

	public Integer getAssociatingComponentId() {
		return associatingComponentId;
	}

	public void setAssociatingComponentId(Integer associatingComponentId) {
		this.associatingComponentId = associatingComponentId;
	}

	public String getAssociatingComponentName() {
		return associatingComponentName;
	}

	public void setAssociatingComponentName(String associatingComponentName) {
		this.associatingComponentName = associatingComponentName;
	}


	public Integer getAssociationId() {
		return associationId;
	}

	public void setAssociationId(Integer associationId) {
		this.associationId = associationId;
	}

	public Integer getAssociatedComponentId() {
		return associatedComponentId;
	}

	public void setAssociatedComponentId(Integer associatedComponentId) {
		this.associatedComponentId = associatedComponentId;
	}

	public Integer getFldSeqNbr() {
		return fldSeqNbr;
	}

	public void setFldSeqNbr(Integer fldSeqNbr) {
		this.fldSeqNbr = fldSeqNbr;
	}

	public Integer getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(Integer environmentId) {
		this.environmentId = environmentId;
	}

}
