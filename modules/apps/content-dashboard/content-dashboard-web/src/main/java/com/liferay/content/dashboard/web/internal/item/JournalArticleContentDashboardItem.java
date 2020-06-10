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

package com.liferay.content.dashboard.web.internal.item;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Cristina González
 */
public class JournalArticleContentDashboardItem
	implements ContentDashboardItem<JournalArticle> {

	public JournalArticleContentDashboardItem(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		JournalArticle journalArticle, Language language) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_journalArticle = journalArticle;
		_language = language;
	}

	@Override
	public Date getExpirationDate() {
		return _journalArticle.getExpirationDate();
	}

	@Override
	public Date getModifiedDate() {
		return _journalArticle.getModifiedDate();
	}

	@Override
	public Date getPublishDate() {
		return _journalArticle.getDisplayDate();
	}

	@Override
	public List<Status> getStatuses(Locale locale) {
		return Collections.singletonList(
			new Status(
				_language.get(
					locale,
					WorkflowConstants.getStatusLabel(
						_journalArticle.getStatus())),
				WorkflowConstants.getStatusStyle(_journalArticle.getStatus())));
	}

	@Override
	public String getStatusLabel(Locale locale) {
		return WorkflowConstants.getStatusLabel(_journalArticle.getStatus());
	}

	@Override
	public String getStatusStyle() {
		return WorkflowConstants.getStatusStyle(_journalArticle.getStatus());
	}

	@Override
	public String getSubtype(Locale locale) {
		DDMStructure ddmStructure = _journalArticle.getDDMStructure();

		return ddmStructure.getName(locale);
	}

	@Override
	public String getTitle(Locale locale) {
		return _journalArticle.getTitle(locale);
	}

	@Override
	public long getUserId() {
		return _journalArticle.getUserId();
	}

	@Override
	public String getUserName() {
		return _journalArticle.getUserName();
	}

	@Override
	public String getViewURL(ThemeDisplay themeDisplay) {
		try {
			ThemeDisplay clonedThemeDisplay =
				(ThemeDisplay)themeDisplay.clone();

			clonedThemeDisplay.setScopeGroupId(_journalArticle.getGroupId());

			return Optional.ofNullable(
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey(), clonedThemeDisplay)
			).orElse(
				StringPool.BLANK
			);
		}
		catch (CloneNotSupportedException cloneNotSupportedException) {
			_log.error(cloneNotSupportedException, cloneNotSupportedException);

			return StringPool.BLANK;
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return StringPool.BLANK;
		}
	}

	@Override
	public boolean isViewURLEnabled(ThemeDisplay themeDisplay) {
		if (!_journalArticle.hasApprovedVersion()) {
			return false;
		}

		if (Validator.isNull(getViewURL(themeDisplay))) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleContentDashboardItem.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final JournalArticle _journalArticle;
	private final Language _language;

}