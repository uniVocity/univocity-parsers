package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/50
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_50 {

	public static class P {
		@Parsed
		public String p;

		@Parsed
		public String q;

	}

	public static class E extends P {
		@Parsed
		private String a;

		@Parsed
		@Format(formats = "dd-MMM-yyyy")
		private Date b;

		@Parsed
		@Format(formats = "dd-MMM-yyyy")
		private Date c;
	}

	private void runTest(boolean strictValidationEnabled){
		final BeanListProcessor<E> processor = new BeanListProcessor<E>(E.class);
		processor.setStrictHeaderValidationEnabled(strictValidationEnabled);

		final CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);

		new CsvParser(settings).parse(new StringReader("q,a,Y,b\n1,a@b.com,blah,25-DEC-2015"));

		E e = processor.getBeans().get(0);
		assertNotNull(e);
		assertNull(e.p);
		assertEquals(e.q, "1");
		assertEquals(e.a, "a@b.com");
		assertNotNull(e.b);
		assertNull(e.c);
	}

	@Test
	public void ensureBeanIsParsedWhenColumnsAreNotPresent() {
		runTest(false);
	}

	@Test
	public void ensureStringValidationStillWorks() {
		try {
			runTest(true);
			fail("Expecting error caused by missing field 'c'");
		} catch(DataProcessingException ex){
			//success!
		}
	}

}
