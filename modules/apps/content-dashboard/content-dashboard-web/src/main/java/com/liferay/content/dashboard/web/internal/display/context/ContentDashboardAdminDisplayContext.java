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

package com.liferay.content.dashboard.web.internal.display.context;

import com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItem;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.info.display.url.provider.InfoEditURLProviderTracker;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class ContentDashboardAdminDisplayContext {

	public ContentDashboardAdminDisplayContext(
		HttpServletRequest httpServletRequest,
		InfoEditURLProviderTracker infoEditURLProviderTracker,
		SearchContainer<ContentDashboardInfoItem<?>> searchContainer) {

		_httpServletRequest = httpServletRequest;
		_infoEditURLProviderTracker = infoEditURLProviderTracker;
		_searchContainer = searchContainer;
	}

	private InfoEditURLProviderTracker _infoEditURLProviderTracker;

	public SearchContainer<ContentDashboardInfoItem<?>> getSearchContainer() {
		return _searchContainer;
	}

	private final SearchContainer<ContentDashboardInfoItem<?>> _searchContainer;

	public String getEditURL(ContentDashboardInfoItem contentDashboardInfoItem)
		throws Exception {

		InfoEditURLProvider infoEditURLProvider =
			_infoEditURLProviderTracker.getInfoEditURLProvider(
				contentDashboardInfoItem.getClassName());

		return infoEditURLProvider.getURL(
			contentDashboardInfoItem.getObject(), _httpServletRequest);

	}

	private final HttpServletRequest _httpServletRequest;
}