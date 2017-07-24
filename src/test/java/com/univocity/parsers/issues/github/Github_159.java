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
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/159
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_159 {

	public static class NameTransformer extends HeaderTransformer {

		private String prefix;

		public NameTransformer(String... args) {
			prefix = args[0];
		}

		@Override
		public String transformName(Field field, String name) {
			return prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		}
	}

	public static class IndexTransformer extends HeaderTransformer {

		private int startFrom;

		public IndexTransformer(String... args) {
			startFrom = Integer.parseInt(args[0]);
		}

		@Override
		public int transformIndex(Field field, int index) {
			return startFrom + index;
		}
	}

	public static class Wheel {
		@Parsed
		String brand;

		@Parsed
		int miles;
	}

	public static class Wheel2 {
		@Parsed(index = 0)
		String brand;

		@Parsed(index = 1)
		int miles;
	}


	public static class Car {
		@Nested(headerTransformer = NameTransformer.class, args = "frontLeftWheel")
		Wheel frontLeft;

		@Nested(headerTransformer = NameTransformer.class, args = "frontRightWheel")
		Wheel frontRight;

		@Nested(headerTransformer = NameTransformer.class, args = "rearLeftWheel")
		Wheel rearLeft;

		@Nested(headerTransformer = NameTransformer.class, args = "rearRightWheel")
		Wheel rearRight;
	}

	public static class Car2 {
		@Nested(headerTransformer = IndexTransformer.class, args = "0")
		Wheel2 frontLeft;

		@Nested(headerTransformer = IndexTransformer.class, args = "2")
		Wheel2 frontRight;

		@Nested(headerTransformer = IndexTransformer.class, args = "4")
		Wheel2 rearLeft;

		@Nested(headerTransformer = IndexTransformer.class, args = "6")
		Wheel2 rearRight;
	}

	@Test
	public void testNestedWithPrefix() throws IOException {
		String input = "frontLeftWheelBrand,frontLeftWheelMiles,frontRightWheelBrand,frontRightWheelMiles,rearLeftWheelBrand,rearLeftWheelMiles,rearRightWheelBrand,rearRightWheelMiles\n" +
				"b,2,b,4,b,6,v,3\n" +
				"c,1,c,3,c,1,z,9\n";

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		List<Car> cars = new CsvRoutines(settings).parseAll(Car.class, new StringReader(input));

		Car car = cars.get(0);

		assertEquals(car.frontLeft.brand, "b");
		assertEquals(car.frontLeft.miles, 2);
		assertEquals(car.frontRight.brand, "b");
		assertEquals(car.frontRight.miles, 4);
		assertEquals(car.rearLeft.brand, "b");
		assertEquals(car.rearLeft.miles, 6);
		assertEquals(car.rearRight.brand, "v");
		assertEquals(car.rearRight.miles, 3);

		car = cars.get(1);

		assertEquals(car.frontLeft.brand, "c");
		assertEquals(car.frontLeft.miles, 1);
		assertEquals(car.frontRight.brand, "c");
		assertEquals(car.frontRight.miles, 3);
		assertEquals(car.rearLeft.brand, "c");
		assertEquals(car.rearLeft.miles, 1);
		assertEquals(car.rearRight.brand, "z");
		assertEquals(car.rearRight.miles, 9);

		StringWriter out = new StringWriter();
		CsvWriterSettings s = new CsvWriterSettings();
		s.getFormat().setLineSeparator("\n");
		s.setHeaderWritingEnabled(true);
		new CsvRoutines(s).writeAll(cars, Car.class, out);
		assertEquals(out.toString(), input);
	}

	@Test
	public void testNestedTransformingIndex() throws IOException {
		String input =
				"b,2,b,4,b,6,v,3\n" +
						"c,1,c,3,c,1,z,9\n";

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		List<Car2> cars = new CsvRoutines(settings).parseAll(Car2.class, new StringReader(input));

		Car2 car = cars.get(0);

		assertEquals(car.frontLeft.brand, "b");
		assertEquals(car.frontLeft.miles, 2);
		assertEquals(car.frontRight.brand, "b");
		assertEquals(car.frontRight.miles, 4);
		assertEquals(car.rearLeft.brand, "b");
		assertEquals(car.rearLeft.miles, 6);
		assertEquals(car.rearRight.brand, "v");
		assertEquals(car.rearRight.miles, 3);

		car = cars.get(1);

		assertEquals(car.frontLeft.brand, "c");
		assertEquals(car.frontLeft.miles, 1);
		assertEquals(car.frontRight.brand, "c");
		assertEquals(car.frontRight.miles, 3);
		assertEquals(car.rearLeft.brand, "c");
		assertEquals(car.rearLeft.miles, 1);
		assertEquals(car.rearRight.brand, "z");
		assertEquals(car.rearRight.miles, 9);

		StringWriter out = new StringWriter();
		CsvWriterSettings s = new CsvWriterSettings();
		s.getFormat().setLineSeparator("\n");
		new CsvRoutines(s).writeAll(cars, Car2.class, out);
		assertEquals(out.toString(), input);
	}

	public static class Car3 {
		List<Wheel> wheels = new ArrayList<Wheel>();

		@Nested(headerTransformer = NameTransformer.class, args = "frontLeftWheel")
		private void setWheel1(Wheel wheel) {
			wheels.add(wheel);
		}

		@Nested(headerTransformer = NameTransformer.class, args = "frontRightWheel")
		private void setWheel2(Wheel wheel) {
			wheels.add(wheel);
		}

		@Nested(headerTransformer = NameTransformer.class, args = "rearLeftWheel")
		private void setWheel3(Wheel wheel) {
			wheels.add(wheel);
		}

		@Nested(headerTransformer = NameTransformer.class, args = "rearRightWheel")
		private void setWheel4(Wheel wheel) {
			wheels.add(wheel);
		}
	}

	@Test
	public void testNestedList() {
		String input = "frontLeftWheelBrand,frontLeftWheelMiles,frontRightWheelBrand,frontRightWheelMiles,rearLeftWheelBrand,rearLeftWheelMiles,rearRightWheelBrand,rearRightWheelMiles\n" +
				"b,2,b,4,b,6,v,3\n" +
				"c,1,c,3,c,1,z,9\n";

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		List<Car3> cars = new CsvRoutines(settings).parseAll(Car3.class, new StringReader(input));
		Car3 car = cars.get(0);

		assertEquals(car.wheels.get(0).brand, "b");
		assertEquals(car.wheels.get(0).miles, 2);
		assertEquals(car.wheels.get(1).brand, "b");
		assertEquals(car.wheels.get(1).miles, 4);
		assertEquals(car.wheels.get(2).brand, "b");
		assertEquals(car.wheels.get(2).miles, 6);
		assertEquals(car.wheels.get(3).brand, "v");
		assertEquals(car.wheels.get(3).miles, 3);

		car = cars.get(1);

		assertEquals(car.wheels.get(0).brand, "c");
		assertEquals(car.wheels.get(0).miles, 1);
		assertEquals(car.wheels.get(1).brand, "c");
		assertEquals(car.wheels.get(1).miles, 3);
		assertEquals(car.wheels.get(2).brand, "c");
		assertEquals(car.wheels.get(2).miles, 1);
		assertEquals(car.wheels.get(3).brand, "z");
		assertEquals(car.wheels.get(3).miles, 9);
	}
}
