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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardInfoItem.class)
public class BlogsEntryContentDashboardInfoItem
	implements ContentDashboardInfoItem<BlogsEntry> {

	@Override
	public User getAuthor(BlogsEntry blogsEntry) {
		try {
			return _userLocalService.getUser(blogsEntry.getUserId());
		}
		catch (PortalException portalException) {
			return null;
		}
	}

	@Override
	public Date getExpiredDate(BlogsEntry blogsEntry) {
		return null;
	}

	@Override
	public Date getModifiedDate(BlogsEntry blogsEntry) {
		return blogsEntry.getModifiedDate();
	}

	@Override
	public Date getPublishDate(BlogsEntry blogsEntry) {
		return blogsEntry.getDisplayDate();
	}

	@Override
	public String getStatusLabel(BlogsEntry blogsEntry, Locale locale) {
		return WorkflowConstants.getStatusLabel(blogsEntry.getStatus());
	}

	@Override
	public String getStatusStyle(BlogsEntry blogsEntry) {
		return WorkflowConstants.getStatusStyle(blogsEntry.getStatus());
	}

	@Override
	public String getTitle(BlogsEntry blogsEntry, Locale locale) {
		return blogsEntry.getTitle();
	}

	@Override
	public String getType(BlogsEntry blogsEntry, Locale locale) {
		return "blog";
	}

	@Reference
	private UserLocalService _userLocalService;

}