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
package com.univocity.parsers.issues.github;


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.text.*;
import java.util.*;

import static com.univocity.parsers.annotations.helpers.MethodDescriptor.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/280
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_287 {

	public static final class Model {
		private String a;
		private int b;
		private String c;

		@Nested
		private TopModel m;

		@Format(formats = "dd MMM yyyy")
		private Date d;

		private Model() {
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public void setC(String c) {
			this.c = c;
		}

		public String getC() {
			return c;
		}
	}

	public static final class TopModel {
		private String e;
		private int f;

		public String getE() {
			return e;
		}

		public void setE(String e) {
			this.e = e;
		}

		public int getF() {
			return f;
		}

		public void setF(int f) {
			this.f = f;
		}
	}

	private void parseWithMapping(BeanListProcessor<Model> processor) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setProcessor(processor);
		settings.getFormat().setLineSeparator("\n");

		new CsvParser(settings).parse(new StringReader("col1,col2,col3,col4\nval1,2,val3,12 Dec 2010"));

		Model instance = processor.getBeans().get(0);
		assertEquals(instance.a, "val1");
		assertEquals(instance.b, 2);
		assertEquals(instance.c, "val3");
		assertNotNull(instance.d);
		assertEquals(TestUtils.formatDate(instance.d), "12-Dec-2010 00:00:00");

		assertNotNull(instance.m);
		assertEquals(instance.m.e, "val1");
		assertEquals(instance.m.f, 2);
	}

	@Test
	public void mapColumnNameToMethodInCode() throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		ColumnMapper mapper = processor.getColumnMapper();
		mapper.attributeToColumnName("a", "col1");
		mapper.attributeToColumnName("b", "col2");
		mapper.attributeToColumnName("c", "col3");
		mapper.attributeToColumnName("d", "col4");
		mapper.attributeToColumnName("m.e", "col1");
		mapper.attributeToColumnName("m.f", "col2");

		parseWithMapping(processor);
	}

	@Test
	public void mapColumnNameToAttributeInCode() throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		ColumnMapper mapper = processor.getColumnMapper();
		mapper.attributeToColumnName("a", "col1");
		mapper.methodToColumnName(setter("setB", int.class), "col2");
		mapper.methodToColumnName(setter("setC", String.class), "col3");
		mapper.attributeToColumnName("d", "col4");
		mapper.methodNameToColumnName("m.setE", "col1");
		mapper.methodToColumnName(setter("m.setF", int.class), "col2");

		parseWithMapping(processor);
	}

	@Test
	public void mapColumnIndexToAttributeInCode() throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		Map<String, Integer> mapping = new HashMap<String, Integer>();
		mapping.put("a", 0);
		mapping.put("b", 1);
		mapping.put("c", 2);
		mapping.put("d", 3);
		mapping.put("m.e", 0);
		mapping.put("m.f", 1);

		processor.getColumnMapper().attributesToIndexes(mapping);

		parseWithMapping(processor);
	}
}
