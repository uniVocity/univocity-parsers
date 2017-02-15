package com.univocity.parsers.issues.support;

import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Ticket_9 {

	@Test
	public void testEscapeSlashTabInTSV() {
		String input = "hello\tworld\tabc\\\td\\\ne\\tf\n";
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setEscapedTabChar('\t');
		settings.setLineJoiningEnabled(true);
		settings.setReadInputOnSeparateThread(false);

		TsvParser parser = new TsvParser(settings);

		StringReader reader = new StringReader(input);

		parser.beginParsing(reader);
		String[] row = parser.parseNext();

		assertEquals(row.length,3);
		assertEquals(row[0], "hello");
		assertEquals(row[1], "world");
		assertEquals(row[2], "abc\td\ne\tf");

		assertNull(parser.parseNext());
		assertTrue(parser.getContext().isStopped());

		TsvWriterSettings writerSettings = new TsvWriterSettings();
		writerSettings.setFormat(settings.getFormat());
		writerSettings.setLineJoiningEnabled(true);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, writerSettings);

		writer.writeRow(row);
		writer.close();

		assertEquals(out.toString(), "hello\tworld\tabc\\\td\\\ne\\\tf\n");

	}


}
