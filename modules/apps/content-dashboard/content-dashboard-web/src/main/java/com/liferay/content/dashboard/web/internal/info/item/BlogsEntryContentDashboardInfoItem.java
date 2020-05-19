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

package com.liferay.content.dashboard.web.internal.info.item;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Cristina González
 */
public class BlogsEntryContentDashboardInfoItem
	implements ContentDashboardInfoItem<BlogsEntry> {

	public BlogsEntryContentDashboardInfoItem(
		BlogsEntry blogsEntry, UserLocalService userLocalService) {

		_blogsEntry = blogsEntry;
		_userLocalService = userLocalService;
	}

	@Override
	public Date getExpiredDate() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return _blogsEntry.getModifiedDate();
	}

	@Override
	public Date getPublishDate() {
		return _blogsEntry.getDisplayDate();
	}

	@Override
	public String getStatusLabel(Locale locale) {
		return WorkflowConstants.getStatusLabel(_blogsEntry.getStatus());
	}

	@Override
	public String getStatusStyle() {
		return WorkflowConstants.getStatusStyle(_blogsEntry.getStatus());
	}

	@Override
	public String getTitle(Locale locale) {
		return _blogsEntry.getTitle();
	}

	@Override
	public String getType(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		return ResourceBundleUtil.getString(resourceBundle, "blog");
	}

	public long getUserId() {
		return _blogsEntry.getUserId();
	}

	private final BlogsEntry _blogsEntry;
	private final UserLocalService _userLocalService;

}