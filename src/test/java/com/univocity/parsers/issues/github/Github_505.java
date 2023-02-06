package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/505
 */
public class Github_505 {

	@Test
	public void testCommentCharWriting() {
		StringWriter sw1 = new StringWriter();
		{
			CsvWriterSettings writerSettings = new CsvWriterSettings();
			writerSettings.setCommentProcessingEnabled(false);
			CsvWriter writer = new CsvWriter(sw1, writerSettings);
			writer.writeRow(new String[]{"#field1", "field2"});
			writer.close();
		}
		assertEquals(sw1.toString(), "#field1,field2\n");

		StringWriter sw2 = new StringWriter();
		{
			CsvWriterSettings writerSettings = new CsvWriterSettings();
			writerSettings.setCommentProcessingEnabled(true);
			CsvWriter writer = new CsvWriter(sw2, writerSettings);
			writer.writeRow(new String[]{"#field1", "field2"});
			writer.close();
		}
		assertEquals(sw2.toString(), "\"#field1\",field2\n");
	}
}
