/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.util;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.util.FileImpl;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Alicia García
 */
public class GoogleCloudNaturalLanguageUtilTest {

	public static final String PLAIN_TEXT_TYPE = "PLAIN_TEXT";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testGetAnnotateImagePayload() throws Exception {
		Mockito.when(
			_fileEntry.getFileVersion()
		).thenReturn(
			_fileVersion
		);

		String randomString = RandomTestUtil.randomString();

		Mockito.when(
			_fileVersion.getContentStream(false)
		).thenReturn(
			new ByteArrayInputStream(randomString.getBytes())
		);

		List<String> actual =
			GoogleCloudNaturalLanguageUtil.splitTextToMaxSizeCall(
				new String(
					FileUtil.getBytes(_fileVersion.getContentStream(false))),
				5000, PLAIN_TEXT_TYPE);

		List<String> expected = new ArrayList<>();

		expected.add(String.join("", _jsonTextWithContent(randomString)));

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSplitTextToMaxSizeCall3List() {
		int max = 250;

		String ram = RandomTestUtil.randomString(
			max - 1 - _jsonTextWithContent("").length);

		String randomString = ram + " " + ram + " " + ram;

		List<String> actual =
			GoogleCloudNaturalLanguageUtil.splitTextToMaxSizeCall(
				randomString, max, PLAIN_TEXT_TYPE);

		Assert.assertEquals("The number of split text is ", 3, actual.size());
	}

	@Test
	public void testSplitTextToMaxSizeCall3WithNewLine() {
		int max = 20;

		String ram = RandomTestUtil.randomString(
			max - _jsonTextWithContent("").length);

		String randomString = ram + System.lineSeparator() + ram + " " + ram;

		List<String> actual =
			GoogleCloudNaturalLanguageUtil.splitTextToMaxSizeCall(
				randomString, max, PLAIN_TEXT_TYPE);

		Assert.assertEquals("The number of split text is ", 3, actual.size());
	}

	private String[] _jsonTextWithContent(String content) {
		return new String[] {
			"{\"document\":{\"type\":\"" + PLAIN_TEXT_TYPE +
				"\",\"content\":\"",
			content, "\"}}"
		};
	}

	@Mock
	private FileEntry _fileEntry;

	@Mock
	private FileVersion _fileVersion;

}