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
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			String originalCanonicalURL = _portal.getCanonicalURL(
				_portal.getCurrentCompleteURL(httpServletRequest), themeDisplay,
				layout, false, false);

			Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
				originalCanonicalURL, themeDisplay, layout);

			LayoutSEOLink layoutSEOLink =
				_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
					layout, _portal.getLocale(httpServletRequest),
					originalCanonicalURL, alternateURLs);

			Set<Locale> availableLocales = _language.getAvailableLocales(
				layout.getGroupId());

			Group pageGroup = layout.getGroup();

			StringBundler sb = new StringBundler();

			_appendOpenGraphTag(
				sb, "og:description",
				layout.getDescription(themeDisplay.getLanguageId()));
			_appendOpenGraphTag(sb, "og:locale", themeDisplay.getLanguageId());
			availableLocales.forEach(
				locale -> _appendOpenGraphTag(
					sb, "og:locale:alternate", locale.toString()));
			_appendOpenGraphTag(
				sb, "og:site_name", pageGroup.getDescriptiveName());
			_appendOpenGraphTag(sb, "og:title", _getTitle(httpServletRequest));
			_appendOpenGraphTag(sb, "og:url", layoutSEOLink.getHref());

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.println(sb.toString());
		}
		catch (PortalException pe) {
			throw new IOException(pe);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	private void _appendOpenGraphTag(
		StringBundler sb, String property, String content) {

		if (Validator.isNotNull(content)) {
			sb.append("<meta property=\"");
			sb.append(property);
			sb.append("\" content=\"");
			sb.append(content);
			sb.append("\">");
		}
	}

	private String _getTitle(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String title = layout.getHTMLTitle(themeDisplay.getLanguageId());

		Group pageGroup = layout.getGroup();

		Locale locale = themeDisplay.getLocale();

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (Validator.isNotNull(portletId) && layout.isSystem() &&
			!layout.isTypeControlPanel() &&
			StringUtil.equals(layout.getFriendlyURL(), "/manage")) {

			title = _portal.getPortletTitle(portletId, locale);
		}
		else if (Validator.isNotNull(themeDisplay.getTilesTitle())) {
			title = _language.get(locale, themeDisplay.getTilesTitle());
		}
		else {
			if (pageGroup.isLayoutPrototype()) {
				title = pageGroup.getDescriptiveName(locale);
			}
			else {
				if (Validator.isNotNull(
						httpServletRequest.getAttribute(WebKeys.PAGE_TITLE))) {

					ListMergeable<String> titleListMergeable =
						(ListMergeable<String>)httpServletRequest.getAttribute(
							WebKeys.PAGE_TITLE);
					title = titleListMergeable.mergeToString(StringPool.SPACE);
				}

				if (Validator.isNotNull(
						httpServletRequest.getAttribute(
							WebKeys.PAGE_SUBTITLE))) {

					ListMergeable<String> titleListMergeable =
						(ListMergeable<String>)httpServletRequest.getAttribute(
							WebKeys.PAGE_SUBTITLE);
					title =
						titleListMergeable.mergeToString(StringPool.SPACE) +
							" - " + title;
				}
			}

			if (HtmlUtil.getHtml() != null) {
				title = HtmlUtil.escape(title);
			}
		}

		String siteName = HtmlUtil.escape(pageGroup.getDescriptiveName());
		Company company = themeDisplay.getCompany();

		if (Validator.isNotNull(title) &&
			!StringUtil.equals(company.getName(), siteName) &&
			!pageGroup.isLayoutPrototype()) {

			title = title + " - " + siteName;
		}

		return title + " - " + company.getName();
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

}