/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

public class TestBean {

	// if the value parsed in the quantity column is "?" or "-", it will be replaced by null.
	@NullString(nulls = {"?", "-"})
	// if a value resolves to null, it will be converted to the String "0".
	@Parsed(defaultNullRead = "0")
	private Integer quantity;   // The attribute type defines which conversion will be executed when processing the value.
	// In this case, IntegerConversion will be used.
	// The attribute name will be matched against the column header in the file automatically.

	@Trim
	@LowerCase
	// the value for the comments attribute is in the column at index 4 (0 is the first column, so this means fifth column in the file)
	@Parsed(index = 4)
	private String comments;

	// you can also explicitly give the name of a column in the file.
	@Parsed(field = "amount")
	private BigDecimal amount;

	@Trim
	@LowerCase
	// values "no", "n" and "null" will be converted to false; values "yes" and "y" will be converted to true
	@BooleanString(falseStrings = {"no", "n", "null"}, trueStrings = {"yes", "y"})
	@Parsed
	private Boolean pending;

	//##CLASS_END

	@Override
	public String toString() {
		return "TestBean [quantity=" + quantity + ", comments=" + comments + ", amount=" + amount + ", pending=" + pending + "]";
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Boolean getPending() {
		return pending;
	}

	public void setPending(Boolean pending) {
		this.pending = pending;
	}

}
