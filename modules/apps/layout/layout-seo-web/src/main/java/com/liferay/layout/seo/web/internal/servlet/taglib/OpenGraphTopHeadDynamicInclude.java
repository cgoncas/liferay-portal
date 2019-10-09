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

import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alicia García
 */
@Component(service = DynamicInclude.class)
public class OpenGraphTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		try {
			if (!themeDisplay.isSignedIn() && layout.isPublicLayout()) {
				String completeURL = PortalUtil.getCurrentCompleteURL(
					httpServletRequest);

				String canonicalURL = PortalUtil.getCanonicalURL(
					completeURL, themeDisplay, layout, false, false);

				Map<Locale, String> alternateURLs = Collections.emptyMap();

				Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(
					themeDisplay.getSiteGroupId());

				if (availableLocales.size() > 1) {
					alternateURLs = PortalUtil.getAlternateURLs(
						canonicalURL, themeDisplay, layout);
				}

				PrintWriter printWriter = httpServletResponse.getWriter();

				for (LayoutSEOLink layoutSEOLink :
						LayoutSEOLinkManagerUtil.getLocalizedLayoutSEOLinks(
							layout, PortalUtil.getLocale(httpServletRequest),
							canonicalURL, alternateURLs)) {

					printWriter.println(_addLinkTag(layoutSEOLink));
				}
			}
		}
		catch (PortalException pe) {
			throw new IOException(pe);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	private String _addLinkTag(LayoutSEOLink layoutSEOLink) {
		StringBuilder sb = new StringBuilder(10);

		sb.append("<link data-senna-track=\"temporary\" ");
		sb.append("href=\"");
		sb.append(layoutSEOLink.getHref());
		sb.append("\" ");

		if (Validator.isNotNull(layoutSEOLink.getHrefLang())) {
			sb.append("hreflang=\"");
			sb.append(layoutSEOLink.getHrefLang());
			sb.append("\" ");
		}

		sb.append("rel=\"");
		sb.append(layoutSEOLink.getRelationship());
		sb.append("\" />");

		return sb.toString();
	}

}