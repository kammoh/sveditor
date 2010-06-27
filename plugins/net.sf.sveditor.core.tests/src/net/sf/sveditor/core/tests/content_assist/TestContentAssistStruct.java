/****************************************************************************
 * Copyright (c) 2008-2010 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.core.tests.content_assist;

import java.util.List;

import junit.framework.TestCase;
import net.sf.sveditor.core.SVCorePlugin;
import net.sf.sveditor.core.StringInputStream;
import net.sf.sveditor.core.content_assist.SVCompletionProposal;
import net.sf.sveditor.core.db.ISVDBFileFactory;
import net.sf.sveditor.core.db.SVDBFile;
import net.sf.sveditor.core.db.SVDBItem;
import net.sf.sveditor.core.scanutils.StringBIDITextScanner;

public class TestContentAssistStruct extends TestCase {
	
	
	/**
	 * Test that basic macro content assist works
	 */
	public void testContentAssistStructTypedef() {
		String doc1 =
			"class foobar;\n" +
			"endclass\n" +
			"\n" +
			"typedef struct {\n" +
			"    int             my_int_field;\n" +
			"    bit             my_bit_field;\n" +
			"} my_struct_t;\n" +
			"\n" +
			"class my_class;\n" +
			"    my_struct_t              my_struct;\n" +
			"\n" +
			"    function void foo();\n" +
			"        my_struct.my_<<MARK>>\n" +
			"    endfunction\n" +
			"\n" +
			"endclass\n"
			;
				
		TextTagPosUtils tt_utils = new TextTagPosUtils(new StringInputStream(doc1));
		ISVDBFileFactory factory = SVCorePlugin.getDefault().createFileFactory(null);
		
		SVDBFile file = factory.parse(tt_utils.openStream(), "doc1");
		StringBIDITextScanner scanner = new StringBIDITextScanner(tt_utils.getStrippedData());
		
		for (SVDBItem it : file.getItems()) {
			System.out.println("    it: " + it.getType() + " " + it.getName());
		}

		TestCompletionProcessor cp = new TestCompletionProcessor(file, new FileIndexIterator(file));
		
		scanner.seek(tt_utils.getPosMap().get("MARK"));

		cp.computeProposals(scanner, file, tt_utils.getLineMap().get("MARK"));
		List<SVCompletionProposal> proposals = cp.getCompletionProposals();
		
		ContentAssistTests.validateResults(new String[] {"my_int_field", "my_bit_field"}, proposals);
	}

	/**
	 * Test that basic macro content assist works
	 */
	public void testContentAssistStructInClassTypedef() {
		String doc1 =
			"class foobar;\n" +
			"endclass\n" +
			"\n" +
			"class my_class;\n" +
			"\n" +
			"    typedef struct {\n" +
			"        int             my_int_field;\n" +
			"        bit             my_bit_field;\n" +
			"    } my_struct_t;\n" +
			"\n" +
			"    my_struct_t              my_struct;\n" +
			"\n" +
			"    function void foo();\n" +
			"        my_struct.my_<<MARK>>\n" +
			"    endfunction\n" +
			"\n" +
			"endclass\n"
			;
				
		TextTagPosUtils tt_utils = new TextTagPosUtils(new StringInputStream(doc1));
		ISVDBFileFactory factory = SVCorePlugin.getDefault().createFileFactory(null);
		
		SVDBFile file = factory.parse(tt_utils.openStream(), "doc1");
		StringBIDITextScanner scanner = new StringBIDITextScanner(tt_utils.getStrippedData());
		
		for (SVDBItem it : file.getItems()) {
			System.out.println("    it: " + it.getType() + " " + it.getName());
		}

		TestCompletionProcessor cp = new TestCompletionProcessor(file, new FileIndexIterator(file));
		
		scanner.seek(tt_utils.getPosMap().get("MARK"));

		cp.computeProposals(scanner, file, tt_utils.getLineMap().get("MARK"));
		List<SVCompletionProposal> proposals = cp.getCompletionProposals();
		
		ContentAssistTests.validateResults(new String[] {"my_int_field", "my_bit_field"}, proposals);
	}

	/**
	 * Test that basic macro content assist works
	 */
	public void testContentAssistStructField() {
		String doc1 =
			"class foobar;\n" +
			"endclass\n" +
			"\n" +
			"\n" +
			"class my_class;\n" +
			"    struct {\n" +
			"        int             my_int_field;\n" +
			"        bit             my_bit_field;\n" +
			"    } my_struct;\n" +
			"\n" +
			"    function void foo();\n" +
			"        my_struct.my_<<MARK>>\n" +
			"    endfunction\n" +
			"\n" +
			"endclass\n"
			;
				
		TextTagPosUtils tt_utils = new TextTagPosUtils(new StringInputStream(doc1));
		ISVDBFileFactory factory = SVCorePlugin.getDefault().createFileFactory(null);
		
		SVDBFile file = factory.parse(tt_utils.openStream(), "doc1");
		StringBIDITextScanner scanner = new StringBIDITextScanner(tt_utils.getStrippedData());
		
		for (SVDBItem it : file.getItems()) {
			System.out.println("    it: " + it.getType() + " " + it.getName());
		}

		TestCompletionProcessor cp = new TestCompletionProcessor(file, new FileIndexIterator(file));
		
		scanner.seek(tt_utils.getPosMap().get("MARK"));

		cp.computeProposals(scanner, file, tt_utils.getLineMap().get("MARK"));
		List<SVCompletionProposal> proposals = cp.getCompletionProposals();
		
		ContentAssistTests.validateResults(new String[] {"my_int_field", "my_bit_field"}, proposals);
	}

}
