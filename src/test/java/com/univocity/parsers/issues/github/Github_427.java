package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/427
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_427 {

	public enum CarType {
		SUV,
		LIMO
	}

	public static class Car {
		@Parsed
		String id;

		@Parsed(field = "carType", defaultNullRead = "SUV")
		private CarType carType;

		@Parsed
		String averageConsumption;

		@Override
		public String toString() {
			return "Car{" +
					"id='" + id + '\'' +
					", carType=" + carType +
					", averageConsumption='" + averageConsumption + '\'' +
					'}';
		}
	}


	@Test
	public void enumerationWithDefaultNullReadTest() {
		String lines = "" +
				"id,carType,averageConsumtion\n" +
				"2,LIMO,15\n" +
				"1,,10";

		List<Car> cars = new CsvRoutines().parseAll(Car.class, new StringReader(lines));
		assertEquals(cars.get(1).carType, CarType.SUV);

	}
}