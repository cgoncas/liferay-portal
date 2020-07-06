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

package com.liferay.content.dashboard.web.internal.search.request;

import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.RowChecker;

import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina Goonzález
 */
public class ContentDashboardItemChecker extends EmptyOnClickRowChecker {

	public ContentDashboardItemChecker(RenderResponse renderResponse) {
		super(renderResponse);

		_renderResponse = renderResponse;
	}

	@Override
	public String getAllRowsCheckBox() {
		return null;
	}

	@Override
	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, ResultRow resultRow) {

		ContentDashboardItem contentDashboardItem =
			(ContentDashboardItem)resultRow.getObject();

		String name = StringBundler.concat(
			_renderResponse.getNamespace(), RowChecker.ROW_IDS,
			ContentDashboardItem.class.getSimpleName());

		return getRowCheckBox(
			httpServletRequest, isChecked(contentDashboardItem),
			isDisabled(contentDashboardItem), name, "0",
			StringPool.OPEN_BRACKET + name + StringPool.CLOSE_BRACKET,
			"'#" + getAllRowIds() + "'", StringPool.BLANK);
	}

	private final RenderResponse _renderResponse;

}