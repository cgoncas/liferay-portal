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

import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class ContentDashboardAdminInfoPanelDisplayContext {

	public ContentDashboardAdminInfoPanelDisplayContext(
		long contentDashboardItemsCount,
		HttpServletRequest httpServletRequest) {

		_contentDashboardItemsCount = contentDashboardItemsCount;
		_httpServletRequest = httpServletRequest;
	}

	public long getContentDashboardItemsCount() {
		return _contentDashboardItemsCount;
	}

	public long getSelectedContentDashboardItemsCount() {
		if (_selectedContentDashboardItemsCount > 0) {
			return _selectedContentDashboardItemsCount;
		}

		String[] rowIds = ParamUtil.getStringValues(
			_httpServletRequest,
			RowChecker.ROW_IDS + ContentDashboardItem.class.getSimpleName());

		_selectedContentDashboardItemsCount = rowIds.length;

		return _selectedContentDashboardItemsCount;
	}

	private final long _contentDashboardItemsCount;
	private final HttpServletRequest _httpServletRequest;
	private long _selectedContentDashboardItemsCount;

}