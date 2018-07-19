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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/121
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_121 {

	public static abstract class Parent {
		@Parsed(field = "Code")
		private String code;
	}

	public static class Child extends Parent {
		@Parsed(field = "Name")
		private String name;
		@Parsed(field = "Description")
		private String description;
	}
	@Test
	public void ensureHierarchyWorksWithMissingColumns() {
		final BeanListProcessor<Child> processor = new BeanListProcessor<Child>(Child.class);

		final CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setProcessor(processor);

		new CsvParser(settings).parse(new StringReader("Name,Description\nA123,Category 1\nB456,Category 2"));

		final List<Child> beans = processor.getBeans();
		assertEquals(beans.size(), 2);
		assertEquals(beans.get(0).name, "A123");
		assertEquals(beans.get(0).description, "Category 1");
		assertEquals(beans.get(1).name, "B456");
		assertEquals(beans.get(1).description, "Category 2");

	}

}
