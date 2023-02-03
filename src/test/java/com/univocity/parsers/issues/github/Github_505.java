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
		StringWriter sw = new StringWriter();
		{
			CsvWriterSettings writerSettings = new CsvWriterSettings();
			writerSettings.setQuoteCommentStartingFirstColumnEnabled(false);
			CsvWriter writer = new CsvWriter(sw, writerSettings);
			writer.writeRow(new String[]{"#field1", "field2", "field3"});
			writer.close();
		}
		StringReader sr1 = new StringReader(sw.toString());
		{
			CsvParserSettings parserSettings = new CsvParserSettings();
			CsvParser parser = new CsvParser(parserSettings);
			List<String[]> rows = parser.parseAll(sr1);
			assertEquals(rows.size(), 0);
		}
		StringReader sr2 = new StringReader(sw.toString());
		{
			CsvParserSettings parserSettings = new CsvParserSettings();
			parserSettings.setCommentProcessingEnabled(false);
			CsvParser parser = new CsvParser(parserSettings);
			List<String[]> rows = parser.parseAll(sr2);
			String[] row = rows.get(0);
			assertEquals(row[0], "#field1");
		}
	}
}
