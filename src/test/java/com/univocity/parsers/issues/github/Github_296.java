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


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/296
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_296 {


	public static class TestDTO implements Serializable {

		@Parsed(field = "location_name")
		private String name;
		@Parsed(field = "location_address")
		private String addressLine1;
		@Parsed(field = "location_city")
		private String city;
		@Parsed(field = "location_state_abbreviation")
		private String state;
		@Parsed(field = "location_country_code")
		private String country;
		@Parsed(field = "location_zipcode")
		private String postalCode;

		@Parsed(field = "location_latitude")
		@Validate
		private Double latitude;

		@Parsed(field = "location_longitude")
		@Validate
		private Double longitude;

		@Parsed(field = "network_name")
		private String ssid;
	}


	@Test
	public void parseValidatedAttributesWithColumnSelection(){
		String input = "#version:1.0\n" +
				"#timestamp:2017-05-29T23:22:22.320Z\n" +
				"#brand:test report    \n" +
				"    network_name,location_name,location_category,location_address,location_zipcode,location_phone_number,location_latitude,location_longitude,location_city,location_state_name,location_state_abbreviation,location_country,location_country_code,pricing_type,wep_key\n" +
				"    \"1 Free WiFi\",\"Test Restaurant\",\"Cafe / Restaurant\",\"Marktplatz 18\",\"1233\",\"+41 263 34 05\",\"1212.15\",\"7.51\",\"Basel\",\"test\",\"BE\",\"India\",\"DE\",\"premium\",\"\"\n" +
				"    \"2 Free WiFi\",\"Test Restaurant\",\"Cafe / Restaurant\",\"Zufikerstrasse 1\",\"1111\",\"+41 631 60 00\",\"11.354\",\"8.12\",\"Bremgarten\",\"test\",\"AG\",\"China\",\"CH\",\"premium\",\"\"\n" +
				"    \"3 Free WiFi\",\"Test Restaurant\",\"Cafe / Restaurant\",\"Chemin de la Fontaine 10\",\"1260\",\"+41 22 361 69\",\"12.34\",\"11.23\",\"Nyon\",\"Vaud\",\"VD\",\"Switzerland\",\"CH\",\"premium\",\"\"\n" +
				"    \"!.oist*~\",\"HoistGroup Office\",\"Office\",\"Chemin de I Etang\",\"CH-1211\",\"\",\"\",\"\",\"test\",\"test\",\"GE\",\"Switzerland\",\"CH\",\"premium\",\"\"\n" +
				"    \"test\",\"tess's Takashiro\",\"Cafe / Restaurant\",\"Test 1-10\",\"870-01\",\"097-55-1808\",\"\",\"\",\"Oita\",\"Oita\",\"OITA\",\"Japan\",\"JP\",\"premium\",\"1234B\"\n" +
				"\n";

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setLineSeparatorDetectionEnabled(true);
		//parserSettings.setColumnReorderingEnabled(false);
		parserSettings.selectFields("network_name", "location_name", "location_address", "location_zipcode",
				"location_latitude", "location_longitude", "location_city", "location_state_abbreviation", "location_country_code");

		final int[] errorCount = new int[1];
		parserSettings.setProcessorErrorHandler(new ProcessorErrorHandler<ParsingContext>() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				errorCount[0]++;
			}
		});


		CsvRoutines parser = new CsvRoutines(parserSettings);
		ResultIterator<TestDTO, ParsingContext> iterator = parser.iterate(TestDTO.class, new StringReader(input)).iterator();

		int invalid = 0;
		int valid = 0;
		int nulls = 0;
		while (iterator.hasNext()) {
			TestDTO dto = iterator.next();
			if (dto != null) {
				valid++;
				if (dto.longitude == null || dto.latitude == null) {
					invalid++;
				}
			} else {
				nulls++;
			}
		}

		assertEquals(errorCount[0], 2);
		assertEquals(nulls, 2);
		assertEquals(invalid, 0);
		assertEquals(valid, 3);

	}

}
