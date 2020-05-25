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

import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author Cristina González
 */
public class ContentDashboardSearcher extends BaseSearcher {

	public static Indexer<?> getInstance(Collection<String> classNames) {
		return new ContentDashboardSearcher(classNames);
	}

	@Override
	public String[] getSearchClassNames() {
		Stream<String> stream = _classNames.stream();

		return stream.toArray(String[]::new);
	}

	private ContentDashboardSearcher(Collection<String> classNames) {
		_classNames = classNames;

		setDefaultSelectedFieldNames(
			Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK, Field.UID);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	private final Collection<String> _classNames;

}