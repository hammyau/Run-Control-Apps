package org.genevaers.genevaio.html;

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


import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.join;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.script;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.tr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.genevaers.genevaio.fieldnodes.ComparisonState;
import org.genevaers.genevaio.fieldnodes.FieldNodeBase;
import org.genevaers.genevaio.fieldnodes.NumericFieldNode;
import org.genevaers.genevaio.fieldnodes.RecordNode;
import org.genevaers.genevaio.fieldnodes.StringFieldNode;
import org.genevaers.genevaio.fieldnodes.FieldNodeBase.FieldNodeType;

import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.TdTag;
import j2html.tags.specialized.ThTag;
import j2html.tags.specialized.TrTag;

public class LTRecordsHTMLWriter {
	
	private static FileWriter fw;
	static String toggleScript = "function toggleDiv(divname) {" +
			"var ele = document.getElementById(divname);" +
			"if (ele.style.display == \"none\") {" +
			"ele.style.display = \"block\";" +
			"}" +
			"else {" +
			"ele.style.display = \"none\";" +
			"}" +
			"}";

	public static void writeFromRecordNodes(Path cwd, RecordNode root, String filename) {

		File output = cwd.resolve(filename).toFile();
		try {
			
			fw = new FileWriter(output);
			fw.write(
					html(
							head(
									meta().withContent("text/html; charset=UTF-8"),
									link().withRel("stylesheet").withType("text/css").withHref("https://www.w3schools.com/w3css/4/w3.css"),
									link().withRel("stylesheet").withType("text/css").withHref("https://www.w3schools.com/lib/w3-colors-flat.css"),
									link().withRel("stylesheet").withType("text/css").withHref("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"),
									script(join(toggleScript)).withLang("Javascript")
							),
							body(
									bodyContent(root)
							)).withStyle("overflow-x: scroll").renderFormatted());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static DivTag bodyContent(RecordNode root) {
		return div(
						h2("Logic Table Records"),
						recordTables(root)
				).withClass("w3-container");
	}
	

	private static DomContent recordTables(RecordNode root) {
		return table(
				tbody(
						each(root.getChildren(), rec  -> getRows(rec))))
				.withClass("w3-table w3-striped w3-border");
	}

	private static UnescapedText getRows(FieldNodeBase rec) {
			return join(getHeaderRow(rec),
						getRow(rec));
	}


	private static TrTag getRow(FieldNodeBase r) {
		return tr( each(r.getChildren(), n -> rowEntry(n)) );
	}

	private static DomContent rowEntry(FieldNodeBase n) {
		if(n.getFieldNodeType() == FieldNodeType.RECORDPART){
			return  each(n.getChildren(), d -> rowEntry(d));
		} else {
			switch(n.getFieldNodeType()) {
				case NUMBERFIELD:
					return td(((NumericFieldNode)n).getValueString()).withCondClass(n.getState() == ComparisonState.ORIGINAL, "w3-pale-blue")
																	.withCondClass(n.getState() == ComparisonState.NEW, "w3-pale-green")
																	.withCondClass(n.getParent().getState() == ComparisonState.DIFF, "w3-pale-red")
																	.withCondClass(n.getState() == ComparisonState.DIFF, "w3-pink");
				case STRINGFIELD:
					return td(((StringFieldNode)n).getValue() ).withCondClass(n.getState() == ComparisonState.ORIGINAL, "w3-pale-blue")
																	.withCondClass(n.getState() == ComparisonState.NEW, "w3-pale-green")
																	.withCondClass(n.getParent().getState() == ComparisonState.DIFF, "w3-pale-red")
																	.withCondClass(n.getState() == ComparisonState.DIFF, "w3-pink");
				case RECORD:
				default:
					return td("Bad Value");
			}
		}
	}

	private static TrTag getHeaderRow(FieldNodeBase fieldNodeBase) {
		return tr( each(fieldNodeBase.getChildren(), n -> headerElement(n)) );
	}

	private static DomContent headerElement(FieldNodeBase n) {
		if(n.getFieldNodeType() == FieldNodeType.RECORDPART){
			return each(n.getChildren(), h -> headerElement(h));
		} else {
			return th(n.getName());
		}
	}

}
