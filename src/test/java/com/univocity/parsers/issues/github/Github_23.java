package com.univocity.parsers.issues.github;

import static org.testng.Assert.*;

import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/23
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_23 {

	public static class AB {

		@Parsed(index = 0)
		private boolean a;

		@Parsed(index = 1)
		private boolean b;

		public AB() {

		}
	}

	@Test
	public void testCaseInsensitiveBooleanConversion() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parseLine("false,true");
		parser.parseLine("TRUE,FALSE");
		parser.parseLine("faLse,True");
		parser.parseLine("tRUE,FAlsE");

		List<AB> beans = beanProcessor.getBeans();

		assertFalse(beans.isEmpty());
		assertEquals(beans.size(), 4);

		assertFalse(beans.get(0).a);
		assertTrue(beans.get(0).b);

		assertTrue(beans.get(1).a);
		assertFalse(beans.get(1).b);

		assertFalse(beans.get(2).a);
		assertTrue(beans.get(2).b);

		assertTrue(beans.get(3).a);
		assertFalse(beans.get(3).b);
	}
}
