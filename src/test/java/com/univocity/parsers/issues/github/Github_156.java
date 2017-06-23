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

import com.univocity.parsers.common.record.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/156
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_156 {

	@Test
	public void parseDatesConcurrently() throws Exception {
		StringReader input = new StringReader("12/12/12\n11/11/11");
		List<Record> records = new CsvParser(new CsvParserSettings()).parseAllRecords(input);
		Record r1 = records.get(0);
		Record r2 = records.get(1);

		r1.getMetaData().convertIndexes(Conversions.toDate("dd/MM/yy")).add(0);

		final List<Exception> errors = new CopyOnWriteArrayList<Exception>();

		class T extends Thread {
			Record record;

			T(Record r) {
				record = r;
			}

			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						record.getDate(0);
						Thread.sleep((long) (Math.random() * 2));
					} catch (Exception e) {
						e.printStackTrace();
						errors.add(e);
					}
				}
			}
		}

		List<T> threadList = new ArrayList<T>();
		for(int i = 0; i < 50; i++){
			threadList.add(new T(r1));
			threadList.add(new T(r2));
		}
		for(T thread : threadList){
			thread.start();
		}
		for(T thread : threadList){
			thread.join();
		}
		assertEquals(errors.size(), 0);
	}
}
