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

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.model.JournalArticle;
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
public class JournalArticleContentDashboardInfoItem
	implements ContentDashboardInfoItem<JournalArticle> {

	@Override
	public User getAuthor(JournalArticle journalArticle) {
		try {
			return _userLocalService.getUser(journalArticle.getUserId());
		}
		catch (PortalException portalException) {
			return null;
		}
	}

	@Override
	public Date getExpiredDate(JournalArticle journalArticle) {
		return journalArticle.getExpirationDate();
	}

	@Override
	public Date getModifiedDate(JournalArticle journalArticle) {
		return journalArticle.getModifiedDate();
	}

	@Override
	public Date getPublishDate(JournalArticle journalArticle) {
		return journalArticle.getDisplayDate();
	}

	@Override
	public String getStatusLabel(JournalArticle journalArticle, Locale locale) {
		return WorkflowConstants.getStatusLabel(journalArticle.getStatus());
	}

	@Override
	public String getStatusStyle(JournalArticle journalArticle) {
		return WorkflowConstants.getStatusStyle(journalArticle.getStatus());
	}

	@Override
	public String getTitle(JournalArticle journalArticle, Locale locale) {
		return journalArticle.getTitle(locale);
	}

	@Override
	public String getType(JournalArticle journalArticle, Locale locale) {
		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		return ddmStructure.getName(locale);
	}

	@Reference
	private UserLocalService _userLocalService;

}