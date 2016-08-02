/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.common.record.RecordMetaData;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * @author Oliver Henlich
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_109 {

    @DataProvider
    public Object[][] inputProvider() {
        return new Object[][] {{"col1, col2\n val1,val2"}, {"col1, col2 ,\n val1,val2 "}};
    }

    @Test(dataProvider = "inputProvider")
    public void testInternal(String input) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        CsvParser csvParser = new CsvParser(settings);

        csvParser.beginParsing(new StringReader(input));
        Record record = csvParser.parseNextRecord();
        RecordMetaData metaData = record.getMetaData();

        assertNotNull(metaData.headers());
        assertTrue(metaData.containsColumn("col1"));
        assertTrue(metaData.containsColumn("col2"));
    }
}
