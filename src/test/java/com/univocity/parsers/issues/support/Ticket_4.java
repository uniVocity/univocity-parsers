/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

package com.univocity.parsers.issues.support;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Ticket_4 {

	public static class DocumentMetadata {

		@Parsed(index = 0)
		private int id;

		@Parsed(index = 1)
		private String name;

		public long checksum = -1;
		public long size = -1;

		@Parsed(index = 2, defaultNullRead = "-1")
		private int referenceId = -1;

		@Parsed(index = 3)
		private String referenceType;

		@Parsed(index = 4)
		private String description;
	}

	@Test
	public void parseTsvBeanWithMoreColumnsThanInput() {
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		BeanListProcessor<DocumentMetadata> beanProcessor = new BeanListProcessor<DocumentMetadata>(DocumentMetadata.class);
		settings.setRowProcessor(beanProcessor);
		TsvParser parser = new TsvParser(settings);
		parser.parseAll(new StringReader("27102	22132639.txt\n27109	22134500.txt"));

		List<DocumentMetadata> beans = beanProcessor.getBeans();

		assertEquals(beans.size(), 2);

		assertEquals(beans.get(0).id, 27102);
		assertEquals(beans.get(0).name, "22132639.txt");

		assertEquals(beans.get(1).id, 27109);
		assertEquals(beans.get(1).name, "22134500.txt");
	}
}
