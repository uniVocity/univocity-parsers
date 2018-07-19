/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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


package com.univocity.parsers.annotations.meta;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class MetaAnnotationTest {

	@Test
	public void testParseWithMetaAnnotation() throws Exception {
		BeanListProcessor<ReplacementBean> rowProcessor = new BeanListProcessor<ReplacementBean>(ReplacementBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(rowProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("a,BB,x,y,z\n`Va`,`Vb`,,,`vc`"));


		List<ReplacementBean> beans = rowProcessor.getBeans();
		assertEquals(beans.size(), 1);

		assertEquals(beans.get(0).a, "Va");
		assertEquals(beans.get(0).b, "VB");
		assertEquals(beans.get(0).c, "VC");

	}

	@Test
	public void testWriteWithMetaAnnotation() throws Exception {
		BeanWriterProcessor<ReplacementBean> rowProcessor = new BeanWriterProcessor<ReplacementBean>(ReplacementBean.class);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(rowProcessor);

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, settings);

		List<ReplacementBean> beans = new ArrayList<ReplacementBean>();

		beans.add(new ReplacementBean("iii4", "blah blah", "`c`"));
		beans.add(new ReplacementBean("zzz7674", "etc", "`c`"));

		writer.processRecordsAndClose(beans);
		assertEquals(out.toString(), "" +
				"iii4,BLAH BLAH,,,C\n" +
				"zzz7674,ETC,,,C\n");
	}
}


