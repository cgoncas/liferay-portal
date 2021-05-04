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

package com.liferay.layout.reports.web.internal.data.provider;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pagespeedonline.v5.PagespeedInsights;
import com.google.api.services.pagespeedonline.v5.model.LighthouseAuditResultV5;
import com.google.api.services.pagespeedonline.v5.model.LighthouseResultV5;
import com.google.api.services.pagespeedonline.v5.model.PagespeedApiPagespeedResponseV5;

import com.liferay.layout.reports.web.internal.model.LayoutReportsIssue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Cristina González
 */
public class LayoutReportsDataProvider {

	public LayoutReportsDataProvider(String apiKey) {
		_apiKey = apiKey;
	}

	public List<LayoutReportsIssue> getLayoutReportsIssues(String url)
		throws LayoutReportsDataProviderException {

		try {
			return _getLayoutReportsIssues(url);
		}
		catch (LayoutReportsDataProviderException
					layoutReportsDataProviderException) {

			throw layoutReportsDataProviderException;
		}
		catch (Exception exception) {
			throw new LayoutReportsDataProviderException(exception);
		}
	}

	public boolean isValidConnection() {
		return Validator.isNotNull(_apiKey);
	}

	public static class LayoutReportsDataProviderException
		extends PortalException {

		public LayoutReportsDataProviderException(Exception exception) {
			super(exception);
		}

		public LayoutReportsDataProviderException(String message) {
			super(message);
		}

	}

	private int _getCount(LighthouseAuditResultV5 lighthouseAuditResultV5) {
		Map<String, Object> details = lighthouseAuditResultV5.getDetails();

		if (details != null) {
			Object items = details.get("items");

			if (items instanceof List) {
				List<?> itemsList = (List)items;

				return itemsList.size();
			}
		}

		float score = GetterUtil.getFloat(lighthouseAuditResultV5.getScore());

		if (score == 0) {
			return 1;
		}

		return 0;
	}

	private List<LayoutReportsIssue> _getLayoutReportsIssues(String url)
		throws Exception {

		if (!isValidConnection()) {
			throw new LayoutReportsDataProviderException("Invalid Connection");
		}

		PagespeedInsights pagespeedInsights = new PagespeedInsights.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			JacksonFactory.getDefaultInstance(),
			request -> request.setConnectTimeout(_TIMEOUT)
		).build();

		PagespeedInsights.Pagespeedapi pagespeedapi =
			pagespeedInsights.pagespeedapi();

		PagespeedInsights.Pagespeedapi.Runpagespeed runpagespeed =
			pagespeedapi.runpagespeed(url);

		runpagespeed.setCategory(
			Arrays.asList("accessibility", "best-practices", "seo"));
		runpagespeed.setKey(_apiKey);

		PagespeedApiPagespeedResponseV5 pagespeedApiPagespeedResponseV5 =
			runpagespeed.execute();

		LighthouseResultV5 lighthouseResultV5 =
			pagespeedApiPagespeedResponseV5.getLighthouseResult();

		Map<String, LighthouseAuditResultV5> lighthouseAuditResultV5s =
			lighthouseResultV5.getAudits();

		return Arrays.asList(
			new LayoutReportsIssue(
				LayoutReportsIssue.Key.ACCESSIBILITY,
				IntStream.of(
					_getCount(lighthouseAuditResultV5s.get("color-contrast")),
					_getCount(lighthouseAuditResultV5s.get("image-alt")),
					_getCount(lighthouseAuditResultV5s.get("input-image-alt")),
					_getCount(lighthouseAuditResultV5s.get("video-caption"))
				).sum()),
			new LayoutReportsIssue(
				LayoutReportsIssue.Key.SEO,
				IntStream.of(
					_getCount(lighthouseAuditResultV5s.get("canonical")),
					_getCount(
						lighthouseAuditResultV5s.get("crawlable-anchors")),
					_getCount(lighthouseAuditResultV5s.get("document-title")),
					_getCount(lighthouseAuditResultV5s.get("font-size")),
					_getCount(lighthouseAuditResultV5s.get("hreflang")),
					_getCount(
						lighthouseAuditResultV5s.get("image-aspect-ratio")),
					_getCount(lighthouseAuditResultV5s.get("is-crawlable")),
					_getCount(lighthouseAuditResultV5s.get("link-text")),
					_getCount(lighthouseAuditResultV5s.get("meta-description")),
					_getCount(lighthouseAuditResultV5s.get("tap-targets"))
				).sum()));
	}

	private static final int _TIMEOUT = 30000;

	private final String _apiKey;

}