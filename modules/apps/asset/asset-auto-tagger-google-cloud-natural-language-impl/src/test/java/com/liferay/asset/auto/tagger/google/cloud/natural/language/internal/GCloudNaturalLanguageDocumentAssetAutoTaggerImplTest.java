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

package com.liferay.asset.auto.tagger.google.cloud.natural.language.internal;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.HttpImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class GCloudNaturalLanguageDocumentAssetAutoTaggerImplTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_gCloudNaturalLanguageDocumentAssetAutoTagger, "_http",
			new HttpImpl());

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testToTagNames() throws Exception {
		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			new String(
				FileUtil.getBytes(
					getClass(), "dependencies/response_classification.json")));

		Set<String> expectedTagNames = new HashSet<>(
			Arrays.asList(
				"Literature", "Teens", "Childrens Literature", "People",
				"Society", "Books", "Kids", "Childrens Interests"));

		Set<String> actualTagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagger, "_toTagNames",
			new Class<?>[] {JSONArray.class, Predicate.class},
			responseJSONObject.getJSONArray("categories"),
			(Predicate<JSONObject>)jsonObject -> true);

		Assert.assertTrue(
			actualTagNames.toString(),
			actualTagNames.containsAll(expectedTagNames));
	}

	private final GCloudNaturalLanguageDocumentAssetAutoTaggerImpl
		_gCloudNaturalLanguageDocumentAssetAutoTagger =
			new GCloudNaturalLanguageDocumentAssetAutoTaggerImpl();

}