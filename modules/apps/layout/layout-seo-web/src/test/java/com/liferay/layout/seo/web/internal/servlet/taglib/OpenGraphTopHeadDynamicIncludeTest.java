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

package com.liferay.layout.seo.web.internal.servlet.taglib;

import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alicia García
 */
public class OpenGraphTopHeadDynamicIncludeTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);

		_locale = LocaleUtil.getDefault();
		_group = _createGroup();
		_layout = _createLayout();
		_themeDisplay = _getThemeDisplay();

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	@Test
	public void testInclude() throws Exception {
		StringWriter stringWriter = new StringWriter();

		_printWriter = new PrintWriter(stringWriter);

		Map<Locale, String> alternateURLs = Collections.emptyMap();
		HttpServletRequest httpServletRequest = _getHttpServletRequest();
		HttpServletResponse httpServletResponse = _getHttpServletResponse();

		Mockito.when(
			_language.getAvailableLocales(Mockito.anyLong())
		).thenReturn(
			_getAvailableLocales()
		);

		Mockito.when(
			_portal.getCanonicalURL(
				Mockito.anyString(), Mockito.eq(_themeDisplay),
				Mockito.eq(_layout), Mockito.eq(false), Mockito.eq(false))
		).thenReturn(
			"http://localhost:8080/home"
		);
		Mockito.when(
			_layoutSEOLinkManager.getLocalizedLayoutSEOLinks(
				Mockito.eq(_layout), Mockito.eq(_locale), Mockito.anyString(),
				Mockito.eq(alternateURLs))
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			_portal.getLocale(httpServletRequest)
		).thenReturn(
			_locale
		);

		ReflectionTestUtil.setFieldValue(
			_openGraphTopHeadDynamicInclude, "_portal", _portal);

		ReflectionTestUtil.setFieldValue(
			_openGraphTopHeadDynamicInclude, "_layoutSEOLinkManager",
			_layoutSEOLinkManager);

		_openGraphTopHeadDynamicInclude.include(
			httpServletRequest, httpServletResponse, null);
		_printWriter = httpServletResponse.getWriter();

		_getProcessedHTML(stringWriter.toString());
	}

	private Group _createGroup() throws PortalException {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getPathFriendlyURL(
				Matchers.eq(false), Matchers.any(ThemeDisplay.class))
		).thenReturn(
			_PUBLIC_FRIENDLY_URL_PATH
		);

		Mockito.when(
			group.getPathFriendlyURL(
				Matchers.eq(true), Matchers.any(ThemeDisplay.class))
		).thenReturn(
			_PRIVATE_FRIENDLY_URL_PATH
		);
		Mockito.when(
			group.getDescriptiveName()
		).thenReturn(
			"name"
		);

		return group;
	}

	private Layout _createLayout() {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.doReturn(
			"description"
		).when(
			layout
		).getDescription(
			_locale.toLanguageTag()
		);

		Mockito.doReturn(
			"title"
		).when(
			layout
		).getHTMLTitle(
			_locale.toLanguageTag()
		);

		Mockito.doReturn(
			_group
		).when(
			layout
		).getGroup();

		return layout;
	}

	private Set<Locale> _getAvailableLocales() {
		Set<Locale> locales = new HashSet<>();

		locales.add(_locale);

		return locales;
	}

	private HttpServletRequest _getHttpServletRequest() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		return httpServletRequest;
	}

	private HttpServletResponse _getHttpServletResponse() throws IOException {
		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		Mockito.doReturn(
			_printWriter
		).when(
			httpServletResponse
		).getWriter();

		return httpServletResponse;
	}

	private void _getProcessedHTML(String stringWriter) {
		Document document = Jsoup.parse(stringWriter);

		document.outputSettings(
			new Document.OutputSettings() {
				{
					prettyPrint(false);
				}
			});

		Elements selectDescription = document.select(
			"meta[property='og:description']");

		Assert.assertNotNull(selectDescription);
		Assert.assertEquals(1, selectDescription.size());
		Element elementDescription = selectDescription.get(0);

		Assert.assertEquals("description", elementDescription.attr("content"));

		Elements selectSiteName = document.select(
			"meta[property='og:site_name']");

		Assert.assertNotNull(selectSiteName);
		Assert.assertEquals(1, selectSiteName.size());
		Element elementSiteName = selectSiteName.get(0);

		Assert.assertEquals("name", elementSiteName.attr("content"));

		Elements selectLocale = document.select("meta[property='og:locale']");

		Assert.assertNotNull(selectLocale);
		Assert.assertEquals(1, selectLocale.size());
		Element elementLocale = selectLocale.get(0);

		Assert.assertEquals(
			_locale.toLanguageTag(), elementLocale.attr("content"));

		Elements selectTitle = document.select("meta[property='og:title']");

		Assert.assertNotNull(selectTitle);
		Assert.assertEquals(1, selectTitle.size());
		Element elementTitle = selectTitle.get(0);

		Assert.assertEquals(
			"title - companyName", elementTitle.attr("content"));

		Elements selectUrl = document.select("meta[property='og:url']");

		Assert.assertNotNull(selectUrl);
		Assert.assertEquals(1, selectUrl.size());
		Element elementUrl = selectUrl.get(0);

		Assert.assertEquals(
			"http://localhost:8080/home", elementUrl.attr("content"));
	}

	private ThemeDisplay _getThemeDisplay() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Mockito.doReturn(
			"companyName"
		).when(
			_company
		).getName();
		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(_locale);
		themeDisplay.setLanguageId(_locale.toLanguageTag());
		themeDisplay.setLayout(_layout);

		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private static final String _PRIVATE_FRIENDLY_URL_PATH = "/group";

	private static final String _PUBLIC_FRIENDLY_URL_PATH = "/web";

	@Mock
	private Company _company;

	private Group _group;

	@Mock
	private Language _language;

	private Layout _layout;

	@Mock
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	private Locale _locale;
	private final OpenGraphTopHeadDynamicInclude
		_openGraphTopHeadDynamicInclude = new OpenGraphTopHeadDynamicInclude();

	@Mock
	private Portal _portal;

	private PrintWriter _printWriter;
	private ThemeDisplay _themeDisplay;

}