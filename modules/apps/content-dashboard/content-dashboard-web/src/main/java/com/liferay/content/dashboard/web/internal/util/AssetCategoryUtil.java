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

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cristina González
 */
public class AssetCategoryUtil {

	public static String toString(
		List<AssetCategory> assetCategories, Locale locale) {

		if (ListUtil.isEmpty(assetCategories)) {
			return StringPool.BLANK;
		}

		Stream<AssetCategory> stream = assetCategories.stream();

		return stream.map(
			assetCategory -> assetCategory.getTitle(locale)
		).collect(
			Collectors.joining(StringPool.COMMA_AND_SPACE)
		);
	}

}