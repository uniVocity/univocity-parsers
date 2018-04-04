/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.annotations;

import com.univocity.parsers.annotations.helpers.AnnotationHelper;

import java.io.*;
import java.lang.reflect.Constructor;

import com.univocity.parsers.annotations.meta.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class AnnotationHelperTest {

	@Test
	public void shouldCreateAnnotationHelper() throws Exception {
		Constructor<AnnotationHelper> c = AnnotationHelper.class.getDeclaredConstructor();
		c.setAccessible(true);
		AnnotationHelper helper = c.newInstance();

		assertNotNull(helper);
	}

	@Test
	public void testContentCleaner() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setDelimiter(';');
		settings.setRowWriterProcessor(new BeanWriterProcessor<CleanBeanTest>(CleanBeanTest.class));

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, settings);

		writer.processRecord(new CleanBeanTest("this;is;a;test", ";and;another;test;", 1));
		writer.processRecord(new CleanBeanTest("this;is;b;test", ";", 2));
		writer.processRecord(new CleanBeanTest("this;is;c;test", ";;", 3));

		writer.close();

		assertEquals(out.toString(), "" +
				"thisistest;ndnothertest;1\n" +
				"thisisbtest;;2\n" +
				"thisisctest;;3");
	}
}
