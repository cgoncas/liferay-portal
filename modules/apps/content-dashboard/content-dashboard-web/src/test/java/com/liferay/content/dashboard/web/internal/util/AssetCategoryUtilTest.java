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

package com.liferay.content.dashboard.web.internal.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Cristina González
 */
public class AssetCategoryUtilTest {

	@Test
	public void testToString() {
		AssetCategory assetCategory = Mockito.mock(AssetCategory.class);

		Mockito.when(
			assetCategory.getTitle(LocaleUtil.US)
		).thenReturn(
			"title"
		);

		Assert.assertEquals(
			"title",
			AssetCategoryUtil.toString(
				Collections.singletonList(assetCategory), LocaleUtil.US));
	}

	@Test
	public void testToStringWithEmptyAssetCategories() {
		Assert.assertEquals(
			StringPool.BLANK,
			AssetCategoryUtil.toString(Collections.emptyList(), LocaleUtil.US));
	}

	@Test
	public void testToStringWithMultipleAssetCategories() {
		AssetCategory assetCategory1 = Mockito.mock(AssetCategory.class);

		Mockito.when(
			assetCategory1.getTitle(LocaleUtil.US)
		).thenReturn(
			"title1"
		);

		AssetCategory assetCategory2 = Mockito.mock(AssetCategory.class);

		Mockito.when(
			assetCategory2.getTitle(LocaleUtil.US)
		).thenReturn(
			"title2"
		);

		Assert.assertEquals(
			"title1, title2",
			AssetCategoryUtil.toString(
				ListUtil.fromArray(assetCategory1, assetCategory2),
				LocaleUtil.US));
	}

}