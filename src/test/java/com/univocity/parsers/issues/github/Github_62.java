/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.io.StringReader;
import java.util.Arrays;

public class Github_62 {

	private static final String INPUT = "#Comment1\n#Comment2\nH1	H2\n#Comment3\nV1	V2\n#Comment4";

	private TsvParser getParser(RowProcessor processor){
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setNumberOfRecordsToRead(0);
		settings.setCommentCollectionEnabled(true);
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		TsvParser parser = new TsvParser(settings);

		return parser;
	}

	@Test
	public void testRead0Records(){
		TsvParser parser = getParser(null);
		parser.beginParsing(new StringReader(INPUT));
		assertNull(parser.parseNext());
		assertEquals(parser.getContext().headers(), new String[]{"H1", "H2"});
		assertEquals(parser.getContext().lastComment(), "Comment3");
		assertTrue(parser.getContext().comments().values().contains("Comment1"));
		assertTrue(parser.getContext().comments().values().contains("Comment2"));
		assertTrue(parser.getContext().comments().values().contains("Comment3"));
		assertFalse(parser.getContext().comments().values().contains("Comment4"));
	}


	@Test
	public void testRead0RecordsWithRowProcessor(){
		TsvParser parser = getParser(new AbstractRowProcessor() {
			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				fail("Should not be called. Got row " + Arrays.toString(row));
			}

			@Override
			public void processEnded(ParsingContext context) {
				assertEquals(context.headers(), new String[]{"H1", "H2"});
				assertEquals(context.lastComment(), "Comment3");
				assertTrue(context.comments().values().contains("Comment1"));
				assertTrue(context.comments().values().contains("Comment2"));
				assertTrue(context.comments().values().contains("Comment3"));
				assertFalse(context.comments().values().contains("Comment4"));
			}
		});

		parser.parse(new StringReader(INPUT));
	}
}
