package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/415
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_415 {
	@Test
	public void detectedFormatTest() {
		String lines =
				" 4509484 2\n"
						+ "user37748\taddress\t0___Ku0GD8\n"
						+ "user37749\taddress\t__We4__E22\n"
						+ "user37750\taddress\tU460436rJK\n"
						+ "user37751\taddress\tFP_6x_d_Mw\n"
						+ "user37752\taddress\t_LZ9_F_9_0\n"
						+ "user37753\taddress\ti___jF54__\n"
						+ "user37754\taddress\t_SBv0pVB__\n"
						+ "user37755\taddress\t5SXcz__f7c\n"
						+ "user37756\taddress\td_2VY__IPe\n"
						+ "user37757\taddress\t3__mC1i__5\n"
						+ "user37758\taddress\tu_cGnJ_7O_\n"
						+ "user37759\taddress\t_E2f76sH_7\n"
						+ "user37760\taddress\t__DsG_wb0N\n"
						+ "user37761\taddress\t__669503_B\n"
						+ "user37762\taddress\t_p8lCr3h9_\n"
						+ "user37763\taddress\ti0MO1Mh8_A\n"
						+ "user37764\taddress\t_2__Yg___4\n"
						+ "user37765\taddress\t__E_10_xwK\n"
						+ "user37766\taddress\tHz__RNGCN_\n";

		StringReader stringReader = new StringReader(lines);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setDelimiterDetectionEnabled(true, '|', '\t');

		CsvParser csvParser = new CsvParser(parserSettings);
		csvParser.parseAll(stringReader);
		CsvFormat detectedFormat = csvParser.getDetectedFormat();

		assertEquals(detectedFormat.getDelimiter(), '\t');
	}
}