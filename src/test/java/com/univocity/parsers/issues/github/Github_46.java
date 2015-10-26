package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/46
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_46 {

	@Headers(sequence = {"id", "timestamp", "symbol", "quantity", "isComplete", "datetime", "number"})
	class BasicTypes {
		@Parsed
		int id = 2;
		@Parsed
		double quantity = 2.4;
		@Parsed
		long timestamp = 33L;
		@Parsed
		String symbol = "S";
		@Parsed
		boolean isComplete = true;
		@Parsed
		int number = 1;
	}


	@Test
	public void testFieldSelectionWithOverriddenHeadersAnnotation() {
		BeanWriterProcessor<BasicTypes> processor = new BeanWriterProcessor<BasicTypes>(BasicTypes.class);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(processor);

		settings.setHeaders("id", "symbol", "timestamp");
		settings.selectFields("timestamp", "id");


		StringWriter out = new StringWriter();
		CsvWriter w = new CsvWriter(out, settings);
		w.writeHeaders();
		w.processRecord(new BasicTypes());
		w.processRecord(new BasicTypes());
		w.close();

		assertEquals(out.toString(), "id,symbol,timestamp\n2,,33\n2,,33\n");
	}

	@Test
	public void testIndexSelectionWithOverriddenHeadersAnnotation() {
		BeanWriterProcessor<BasicTypes> processor = new BeanWriterProcessor<BasicTypes>(BasicTypes.class);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(processor);

		settings.setHeaders("id", "symbol", "timestamp");
		settings.selectIndexes(2, 0);

		StringWriter out = new StringWriter();
		CsvWriter w = new CsvWriter(out, settings);
		w.writeHeaders();
		w.processRecord(new BasicTypes());
		w.processRecord(new BasicTypes());
		w.close();

		assertEquals(out.toString(), "id,symbol,timestamp\n2,,33\n2,,33\n");
	}
}