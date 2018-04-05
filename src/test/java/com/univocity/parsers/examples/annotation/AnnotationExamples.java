/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.examples.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class AnnotationExamples extends Example {

	private FixedWidthParserSettings getSettings(FixedWidthFields fields) {
		FixedWidthParserSettings settings = new FixedWidthParserSettings(fields);
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setPadding('.');
		settings.setHeaderExtractionEnabled(true);
		return settings;
	}

	private FixedWidthParserSettings getBasicProfileSettings() {
		FixedWidthFields fields = new FixedWidthFields();
		fields.addField("profile_id", 12);
		fields.addField("username", 15);
		fields.addField("followers", 10);
		return getSettings(fields);
	}

	private FixedWidthParserSettings getBasicProfile2Settings() {
		FixedWidthFields fields = new FixedWidthFields();
		fields.addField("id", 12);
		fields.addField("user", 15);
		fields.addField("created_at", 12);
		fields.addField("fees", 9);
		fields.addField("type", 6);
		fields.addField("admin", 5);

		return getSettings(fields);
	}

	@Test
	public void parseProfileByFieldName() {
		Reader input = getReader("/examples/annotation/basic_profile.txt");

		for (ProfileByFieldName profile : new FixedWidthRoutines(getBasicProfileSettings()).iterate(ProfileByFieldName.class, input)) {
			println(profile);
		}
		printAndValidate();
	}

	@Test
	public void parseProfileByFieldPosition() {
		Reader input = getReader("/examples/annotation/basic_profile.txt");

		for (ProfileByFieldPosition profile : new FixedWidthRoutines(getBasicProfileSettings()).iterate(ProfileByFieldPosition.class, input)) {
			println(profile);
		}

		printAndValidate();
	}

	@Test
	public void parseProfileByMultipleFieldNames() {
		Reader input;

		input = getReader("/examples/annotation/basic_profile_2.txt");
		for (ProfileByMultipleFieldNames profile : new FixedWidthRoutines(getBasicProfile2Settings()).iterate(ProfileByMultipleFieldNames.class, input)) {
			println(profile);
		}

		input = getReader("/examples/annotation/basic_profile.txt");
		for (ProfileByMultipleFieldNames profile : new FixedWidthRoutines(getBasicProfileSettings()).iterate(ProfileByMultipleFieldNames.class, input)) {
			println(profile);
		}

		printAndValidate();
	}

	@Test
	public void parseProfileWithDate() {
		Reader input;

		input = getReader("/examples/annotation/basic_profile_2.txt");
		for (ProfileWithDate profile : new FixedWidthRoutines(getBasicProfile2Settings()).iterate(ProfileWithDate.class, input)) {
			println(profile);
		}

		printAndValidate();
	}

	@Test
	public void parseProfile() {
		Reader input;

		input = getReader("/examples/annotation/basic_profile_2.txt");
		for (Profile profile : new FixedWidthRoutines(getBasicProfile2Settings()).iterate(Profile.class, input)) {
			println(profile);
		}
		printAndValidate();
	}
}
