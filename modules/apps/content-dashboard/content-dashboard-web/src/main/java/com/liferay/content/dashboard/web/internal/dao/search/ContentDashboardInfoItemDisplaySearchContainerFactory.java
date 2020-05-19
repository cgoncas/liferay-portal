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

package com.liferay.content.dashboard.web.internal.dao.search;

import com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItem;
import com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItemFactory;
import com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItemFactoryTracker;
import com.liferay.content.dashboard.web.internal.util.ContentDashboardSearcher;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Cristina González
 */
public class ContentDashboardInfoItemDisplaySearchContainerFactory {

	public static ContentDashboardInfoItemDisplaySearchContainerFactory
		getInstance(
			RenderRequest renderRequest, RenderResponse renderResponse,
			ContentDashboardInfoItemFactoryTracker
				contentDashboardInfoItemFactoryTracker,
			Portal portal) {

		return new ContentDashboardInfoItemDisplaySearchContainerFactory(
			renderRequest, renderResponse,
			contentDashboardInfoItemFactoryTracker, portal);
	}

	public ContentDashboardInfoItemDisplaySearchContainerFactory(
		RenderRequest renderRequest, RenderResponse renderResponse,
		ContentDashboardInfoItemFactoryTracker
			contentDashboardInfoItemFactoryTracker,
		Portal portal) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_contentDashboardInfoItemFactoryTracker =
			contentDashboardInfoItemFactoryTracker;
		_portal = portal;
	}

	public SearchContainer<ContentDashboardInfoItem<?>> create()
		throws PortletException {

		PortletURL portletURL = PortletURLUtil.clone(
			_renderResponse.createRenderURL(), _renderResponse);

		SearchContainer<ContentDashboardInfoItem<?>> searchContainer =
			new SearchContainer<>(
				_renderRequest, portletURL, null, "there-are-no-contents");

		String orderByCol = ParamUtil.getString(
			_renderRequest, "orderByCol", "title");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			_renderRequest, "orderByType", "asc");

		searchContainer.setOrderByType(orderByType);

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		Hits hits = _getHits(
			orderByCol, orderByType, _portal.getLocale(_renderRequest),
			searchContainer.getEnd(), searchContainer.getStart());

		searchContainer.setTotal(hits.getLength());
		searchContainer.setResults(
			_map(hits, _portal.getLocale(_renderRequest)));

		return searchContainer;
	}

	private Hits _getHits(
			String orderByCol, String orderByType, Locale locale, int end,
			int start)
		throws PortletException {

		Indexer<?> indexer = ContentDashboardSearcher.getInstance(
			_contentDashboardInfoItemFactoryTracker.getClassNames());

		SearchContext searchContext = SearchContextFactory.getInstance(
			_portal.getHttpServletRequest(_renderRequest));

		searchContext.setGroupIds(null);
		searchContext.setEnd(end);
		searchContext.setStart(start);
		searchContext.setAttribute("status", WorkflowConstants.STATUS_ANY);
		searchContext.setAttribute("latest", Boolean.TRUE);
		searchContext.setKeywords(
			ParamUtil.getString(_renderRequest, "keywords"));
		searchContext.setSorts(_getSort(orderByCol, orderByType, locale));

		try {
			return indexer.search(searchContext);
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private Sort _getSort(
		String orderByCol, String orderByType, Locale locale) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("title")) {
			String sortFieldName = Field.getSortableFieldName(
				"localized_title_".concat(LocaleUtil.toLanguageId(locale)));

			return new Sort(sortFieldName, Sort.STRING_TYPE, !orderByAsc);
		}

		return new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
	}

	private List<ContentDashboardInfoItem<?>> _map(Hits hits, Locale locale) {
		List<SearchResult> searchResults = SearchResultUtil.getSearchResults(
			hits, locale);

		Stream<SearchResult> stream = searchResults.stream();

		return stream.map(
			this::_toContentDashboardInfoItemDisplay
		).filter(
			optional -> optional.isPresent()
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	private <T> Optional<ContentDashboardInfoItem<T>>
		_toContentDashboardInfoItemDisplay(
			ContentDashboardInfoItemFactory<T> contentDashboardInfoItemFactory,
			SearchResult searchResult) {

		try {
			return Optional.of(
				contentDashboardInfoItemFactory.create(
					searchResult.getClassPK()));
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return Optional.empty();
		}
	}

	private <T> Optional<ContentDashboardInfoItem<T>>
		_toContentDashboardInfoItemDisplay(SearchResult searchResult) {

		Optional<ContentDashboardInfoItemFactory<T>>
			contentDashboardInfoItemFactoryOptional =
				_contentDashboardInfoItemFactoryTracker.
					getContentDashboardInfoItemFactoryOptional(
						searchResult.getClassName());

		return contentDashboardInfoItemFactoryOptional.map(
			contentDashboardInfoItemFactory ->
				_toContentDashboardInfoItemDisplay(
					contentDashboardInfoItemFactory, searchResult)
		).orElse(
			Optional.empty()
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardInfoItemDisplaySearchContainerFactory.class);

	private final ContentDashboardInfoItemFactoryTracker
		_contentDashboardInfoItemFactoryTracker;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}