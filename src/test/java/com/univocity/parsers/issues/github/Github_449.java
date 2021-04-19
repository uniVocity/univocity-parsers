package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/438
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_449 {

	@Test
	public void testNoExceptionParsingLine(){
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setDelimiter("|");
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setInputBufferSize(128);

		CsvParser parser = new CsvParser(settings);
		String line = "XX   |XXX-XXXX            |XXXXXX              " +
				"|XXXXXXXX|XXXXX               |XXXXXX              " +
				"|X|XXXXXXX|XXXXXXXX|XXXX|XXXXXXXXXXXXXXX     |XXXXXXXXXXX" +
				"|XXXXXX              |XXXXXXXXXXXXXXXXXXXXXX|XXXXXX              " +
				"|XXXXXXXXXXXXXX|XXXXXX              |XXXXXXXXXXXXXXXXXXXXXX" +
				"|XXXXXX              |XXXXXXXXXXXXXXXXXXXXXX|XXXXXX              " +
				"|XXXXXXXXX|XXXXXX              |XXXXXXX|                    " +
				"||                    ||                    " +
				"||                    ||XXXX-XX-XX 00:00:00.0000000" +
				"||XXXXX.XXXXXXXXXXXXXXX|XXXXX.XXXXXXXXXXXXXX" +
				"|XXXXX.XXXXXXXXXXXXXXX|X|XXXXXX              |X";

		parser.parseLine(line);
	}
}