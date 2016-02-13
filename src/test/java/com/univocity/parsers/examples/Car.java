/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.examples;

import com.univocity.parsers.annotations.*;

import java.math.*;
import java.util.*;

public class Car {
	@Parsed
	private Integer year;

	@Convert(conversionClass = WordsToSetConversion.class, args = {",", "true"})
	@Parsed
	private Set<String> description;

	//##CLASS_END

	@Parsed
	private String make;

	@Parsed
	private String model;

	@Parsed
	private BigDecimal price;

	public Car() {

	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Set<String> getDescription() {
		return description;
	}

	public void setDescription(Set<String> description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Car: {year=" + year + ", make=" + make + ", model=" + model + ", price=" + price+"}";
	}

}
