package com.univocity.parsers.issues.github;

import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/447
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_447 {

	@Test
	public void parseCSV() {
		String input = "" +
				"#\n" +
				"# underscores are used as the padding character, so leading/trailing whitespace can be considered part of the value\n" +
				"#\n" +
				"#4    5     40      40      8";

		final FixedWidthParserSettings settings = new FixedWidthParserSettings(new FixedWidthFields(4, 5, 40, 40, 8));
		settings.setCommentCollectionEnabled(true, true);
		settings.getFormat().setComment('#');
		settings.getFormat().setNormalizedNewline('\n');
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderExtractionEnabled(true);

		final FixedWidthParser parser = new FixedWidthParser(settings);
		parser.parse(new StringReader(input));

		assertEquals(parser.getContext().comments().size(), 4);

	}
}