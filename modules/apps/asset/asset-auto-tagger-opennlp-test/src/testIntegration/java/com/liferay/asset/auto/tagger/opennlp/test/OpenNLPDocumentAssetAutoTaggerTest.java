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

package com.liferay.asset.auto.tagger.opennlp.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.auto.tagger.opennlp.api.OpenNLPDocumentAssetAutoTagger;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class OpenNLPDocumentAssetAutoTaggerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetTagNamesWithTextFile() throws Exception {
		String fileName =
			"Alice's Adventures in Wonderland, by Lewis Carroll.txt";

		Collection<String> expectedTagNames = Arrays.asList(
			"ADVENTURES", "AT ALL.", "Adventures", "Ah", "Alice", "Alice .",
			"Archive Foundation", "Australia", "Beau--ootiful", "Bill",
			"CHAPTER", "Cheshire Cat", "Dr. Gregory B. Newby Chief Executive",
			"Edgar Atheling", "Foundation", "General Information About Project",
			"General Terms", "Geography", "Herald", "I", "IF", "IRS",
			"Internal Revenue Service", "King", "Latitude", "Laughing",
			"Lewis Carroll", "Lewis Carroll Posting Date", "Lewis Carroll This",
			"Lizard", "London", "MINE", "Ma !", "Mary Ann", "Michael Hart",
			"Michael S. Hart", "Mississippi", "NOT", "New Zealand", "Paris",
			"Pat", "Pat !", "Pepper", "Pray",
			"Project Gutenberg Literary Archive Foundation",
			"Project Gutenberg Literary Archive Foundation Project " +
				"Gutenberg-tm",
			"Project Gutenberg-tm", "Public Domain", "Queen", "Queens",
			"Rabbit", "Rome", "Salt Lake City", "Shakespeare", "Shark", "Soup",
			"THERE", "The", "United States", "VERY", "WOULD", "White Rabbit",
			"Whoever", "William", "YOU.--Come", "YOUR");

		Collection<String> actualTagNames =
			_openNLPDocumentAssetAutoTagger.getTagNames(
				new String(
					FileUtil.getBytes(getClass(), "dependencies/" + fileName)),
				0.1F, ContentTypes.TEXT_PLAIN);

		Assert.assertEquals(
			actualTagNames.toString(), expectedTagNames.size(),
			actualTagNames.size());
		Assert.assertTrue(actualTagNames.containsAll(expectedTagNames));
	}

	@Test
	public void testGetTagNamesWithUnsupportedFile() throws Exception {
		String fileName = "test.jpg";

		Collection<String> tagNames =
			_openNLPDocumentAssetAutoTagger.getTagNames(
				new String(
					FileUtil.getBytes(getClass(), "dependencies/" + fileName)),
				0.1F, ContentTypes.IMAGE_JPEG);

		Assert.assertEquals(tagNames.toString(), 0, tagNames.size());
	}

	@Inject
	private OpenNLPDocumentAssetAutoTagger _openNLPDocumentAssetAutoTagger;

}