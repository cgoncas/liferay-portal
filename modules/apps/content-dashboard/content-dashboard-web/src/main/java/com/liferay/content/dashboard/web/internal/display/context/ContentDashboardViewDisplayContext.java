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
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Cristina González
 */
public class ContentDashboardViewDisplayContext {

	public ContentDashboardViewDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		SearchContainer<ContentDashboardInfoItem<?>> searchContainer) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_searchContainer = searchContainer;
	}

	public SearchContainer<ContentDashboardInfoItem<?>> getSearchContainer() {
		return _searchContainer;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardViewDisplayContext.class);

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final SearchContainer<ContentDashboardInfoItem<?>> _searchContainer;

}