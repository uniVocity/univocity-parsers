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


import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static com.univocity.parsers.annotations.helpers.MethodDescriptor.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/287
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_287 {

	public static final class Model {
		private String a;
		private int b;
		private String c;

		private TopModel m;
		private MissUniverse u;

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
		private MissUniverse m;

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

	public static final class MissUniverse {
		private Date g;

		private int h;

		public Date getG() {
			return g;
		}

		public void setG(Date g) {
			this.g = g;
		}

		public int getH() {
			return h;
		}

		public void setH(Long h) {
			this.h = h.intValue();
		}
	}

	private void parseWithMapping(BeanListProcessor<Model> processor) {
		processor.convertFields(Conversions.toDate("dd MMM yyyy")).set("col4");
		processor.convertFields(Conversions.toDate("yyyy-MM-dd")).set("col5");


		CsvParserSettings settings = new CsvParserSettings();
		settings.setProcessor(processor);
		settings.getFormat().setLineSeparator("\n");

		new CsvParser(settings).parse(new StringReader("col1,col2,col3,col4,col5\nval1,2,val3,12 Dec 2010,2006-06-06"));

		Model instance = processor.getBeans().get(0);
		assertEquals(instance.a, "val1");
		assertEquals(instance.b, 2);
		assertEquals(instance.c, "val3");
		assertNotNull(instance.d);
		assertEquals(TestUtils.formatDate(instance.d), "12-Dec-2010 00:00:00");

		assertNotNull(instance.m);
		assertEquals(instance.m.e, "val1");
		assertEquals(instance.m.f, 2);

		assertNotNull(instance.u);
		assertEquals(TestUtils.formatDate(instance.u.g), "12-Dec-2010 00:00:00");
		assertEquals(instance.u.h, 2);

		assertNotNull(instance.m.m);
		assertEquals(TestUtils.formatDate(instance.m.m.g), "06-Jun-2006 00:00:00");
		assertEquals(instance.m.m.h, 2);
	}

	@Test
	public void mapColumnNameToAttributeInCode () throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		ColumnMapper mapper = processor.getColumnMapper();
		//model
		mapper.attributeToColumnName("a", "col1");
		mapper.attributeToColumnName("b", "col2");
		mapper.attributeToColumnName("c", "col3");
		mapper.attributeToColumnName("d", "col4");

		//model.topmodel
		mapper.attributeToColumnName("m.e", "col1");
		mapper.attributeToColumnName("m.f", "col2");

		//model.topmodel.missuniverse
		mapper.attributeToColumnName("m.m.g", "col5");
		mapper.attributeToColumnName("m.m.h", "col2");

		//model.missuniverse
		mapper.attributeToColumnName("u.g", "col4");
		mapper.attributeToColumnName("u.h", "col2");

		parseWithMapping(processor);
	}

	@Test
	public void mapColumnNameToMethodInCode() throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		ColumnMapper mapper = processor.getColumnMapper();

		//model
		mapper.attributeToColumnName("a", "col1");
		mapper.setterToColumnName("setB", int.class, "col2");
		mapper.methodToColumnName(setter("setC", String.class), "col3");
		mapper.attributeToColumnName("d", "col4");

		//model.topmodel
		mapper.methodNameToColumnName("m.setE", "col1");
		mapper.methodToColumnName(setter("m.setF", int.class), "col2");

		//model.topmodel.missuniverse
		mapper.methodNameToColumnName("m.m.setG", "col5");
		mapper.setterToColumnName("m.m.setH", Long.class, "col2");

		//model.missuniverse
		mapper.setterToColumnName("u.setG", Date.class, "col4");
		mapper.methodNameToColumnName("u.setH", "col2");

		parseWithMapping(processor);
	}

	@Test
	public void mapColumnIndexToMethodInCode() throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		ColumnMapper mapper = processor.getColumnMapper();

		//model
		mapper.attributeToIndex("a", 0);
		mapper.setterToIndex("setB", int.class, 1);
		mapper.methodToIndex(setter("setC", String.class), 2);
		mapper.attributeToIndex("d", 3);


		//model.topmodel
		mapper.methodNameToIndex("m.setE", 0);
		mapper.methodToIndex(setter("m.setF", int.class), 1);

		//model.topmodel.missuniverse
		mapper.methodNameToIndex("m.m.setG", 4);
		mapper.setterToIndex("m.m.setH", Long.class, 1);

		//model.missuniverse
		mapper.setterToIndex("u.setG", Date.class, 3);
		mapper.methodNameToIndex("u.setH", 1);

		parseWithMapping(processor);
	}

}
