/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/143
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_149 {

	@Headers(sequence = {"bankCode", "batchCode", "registerType"}, extract = false, write = false)
	public interface Header {

	}

	public static class BankHeader implements Header {
		@Parsed
		@FixedWidth(value = 3, alignment = FieldAlignment.RIGHT, padding = '0')
		private Integer bankCode;

		@Override
		public String toString() {
			return String.valueOf(bankCode);
		}
	}

	public static class FileHeader implements Header {
		@Parsed
		@FixedWidth(value = 3, alignment = FieldAlignment.RIGHT, padding = '0')
		private Integer bankCode;

		@Parsed
		@FixedWidth(value = 4, alignment = FieldAlignment.RIGHT, padding = '0')
		private Integer batchCode;

		@Parsed
		@FixedWidth(value = 1, alignment = FieldAlignment.RIGHT, padding = '0')
		private Integer registerType;


		@Override
		public String toString() {
			return bankCode + "," + batchCode + "," + registerType;
		}
	}

	@Test
	public void multiBeanSupportsHeadersAnnotation() throws Exception {
		FixedWidthParserSettings settings = new FixedWidthParserSettings();
		settings.setAutoConfigurationEnabled(true);
		settings.setHeaderExtractionEnabled(false);
		settings.getFormat().setLineSeparator("\n");

		MultiBeanListProcessor processor = new MultiBeanListProcessor(FileHeader.class, BankHeader.class);
		settings.setProcessor(processor);

		FixedWidthParser parser = new FixedWidthParser(settings);     // Here should call configureFromAnnotations

		parser.parse(new StringReader("11122223\n4  55   \n6  7   8\n"));

		List<FileHeader> fileList = processor.getBeans(FileHeader.class);
		assertEquals(fileList.toString(), "[111,2222,3, 4,55,null, 6,7,8]");

		List<BankHeader> bankList = processor.getBeans(BankHeader.class);
		assertEquals(bankList.toString(), "[111, 4, 6]");

	}
}
