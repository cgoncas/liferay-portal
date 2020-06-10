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
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Cristina González
 */
public class JournalArticleContentDashboardItemTest {

	@Test
	public void testGetExpirationDate() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			journalArticle.getExpirationDate(),
			journalArticleContentDashboardItem.getExpirationDate());
	}

	@Test
	public void testGetModifiedDate() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			journalArticle.getModifiedDate(),
			journalArticleContentDashboardItem.getModifiedDate());
	}

	@Test
	public void testGetPublishDate() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			journalArticle.getDisplayDate(),
			journalArticleContentDashboardItem.getPublishDate());
	}

	@Test
	public void testGetStatuses() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(
				null, journalArticle, _getLanguage());

		List<ContentDashboardItem.Status> statuses =
			journalArticleContentDashboardItem.getStatuses(LocaleUtil.US);

		Assert.assertEquals(statuses.toString(), 1, statuses.size());

		ContentDashboardItem.Status status = statuses.get(0);

		Assert.assertEquals(
			WorkflowConstants.LABEL_APPROVED, status.getLabel());
		Assert.assertEquals("success", status.getStyle());
	}

	@Test
	public void testGetStatusLabel() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			WorkflowConstants.LABEL_APPROVED,
			journalArticleContentDashboardItem.getStatusLabel(LocaleUtil.US));
	}

	@Test
	public void testGetStatusStyle() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			"success", journalArticleContentDashboardItem.getStatusStyle());
	}

	@Test
	public void testGetSubtype() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			"subtype",
			journalArticleContentDashboardItem.getSubtype(LocaleUtil.US));
	}

	@Test
	public void testGetTitle() {
		JournalArticle journalArticle = _getJournalArticle();

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(null, journalArticle, null);

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			journalArticleContentDashboardItem.getTitle(LocaleUtil.US));
	}

	@Test
	public void testGetViewURL() throws Exception {
		JournalArticle journalArticle = _getJournalArticle();

		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider = Mockito.mock(
				AssetDisplayPageFriendlyURLProvider.class);

		Mockito.when(
			assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyObject())
		).thenReturn(
			"validURL"
		);

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(
				assetDisplayPageFriendlyURLProvider, journalArticle, null);

		Assert.assertEquals(
			"validURL",
			journalArticleContentDashboardItem.getViewURL(_getThemeDisplay()));
	}

	@Test
	public void testGetViewURLWithNullFriendlyURL()
		throws CloneNotSupportedException {

		JournalArticle journalArticle = _getJournalArticle();

		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider = Mockito.mock(
				AssetDisplayPageFriendlyURLProvider.class);

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(
				assetDisplayPageFriendlyURLProvider, journalArticle, null);

		Assert.assertEquals(
			StringPool.BLANK,
			journalArticleContentDashboardItem.getViewURL(_getThemeDisplay()));
	}

	@Test
	public void testIsViewURLEnabled() throws Exception {
		JournalArticle journalArticle = _getJournalArticle();

		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider = Mockito.mock(
				AssetDisplayPageFriendlyURLProvider.class);

		Mockito.when(
			assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyObject())
		).thenReturn(
			"validURL"
		);

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(
				assetDisplayPageFriendlyURLProvider, journalArticle, null);

		Assert.assertTrue(
			journalArticleContentDashboardItem.isViewURLEnabled(
				_getThemeDisplay()));
	}

	@Test
	public void testIsViewURLEnabledWithNotApprovedVersion() {
		JournalArticle journalArticle = _getJournalArticle();

		Mockito.when(
			journalArticle.hasApprovedVersion()
		).thenReturn(
			false
		);

		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider = Mockito.mock(
				AssetDisplayPageFriendlyURLProvider.class);

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(
				assetDisplayPageFriendlyURLProvider, journalArticle, null);

		Assert.assertFalse(
			journalArticleContentDashboardItem.isViewURLEnabled(null));
	}

	@Test
	public void testIsViewURLEnabledWithNullFriendlyURL()
		throws CloneNotSupportedException {

		JournalArticle journalArticle = _getJournalArticle();

		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider = Mockito.mock(
				AssetDisplayPageFriendlyURLProvider.class);

		JournalArticleContentDashboardItem journalArticleContentDashboardItem =
			new JournalArticleContentDashboardItem(
				assetDisplayPageFriendlyURLProvider, journalArticle, null);

		Assert.assertFalse(
			journalArticleContentDashboardItem.isViewURLEnabled(
				_getThemeDisplay()));
	}

	private JournalArticle _getJournalArticle() {
		JournalArticle journalArticle = Mockito.mock(JournalArticle.class);

		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		Mockito.when(
			ddmStructure.getName(Mockito.any(Locale.class))
		).thenReturn(
			"subtype"
		);

		Mockito.when(
			journalArticle.getDDMStructure()
		).thenReturn(
			ddmStructure
		);

		Mockito.when(
			journalArticle.getDisplayDate()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			journalArticle.getExpirationDate()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			journalArticle.hasApprovedVersion()
		).thenReturn(
			true
		);

		Mockito.when(
			journalArticle.isApproved()
		).thenReturn(
			true
		);

		Mockito.when(
			journalArticle.getModifiedDate()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			journalArticle.getTitle(Mockito.any(Locale.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return journalArticle;
	}

	private Language _getLanguage() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArguments()[1]
		);

		return language;
	}

	private ThemeDisplay _getThemeDisplay() throws CloneNotSupportedException {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.clone()
		).thenReturn(
			themeDisplay
		);

		return themeDisplay;
	}

}