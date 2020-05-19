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

import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.content.dashboard.web.internal.display.ContentDashboardInfoItemDisplay;
import com.liferay.content.dashboard.web.internal.info.item.util.ContentDashboardInfoItemDisplayMapper;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Collection;
import java.util.stream.Stream;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Cristina González
 */
public class ContentDashboardInfoItemDisplaySearchContainerFactory {

	public static SearchContainer<ContentDashboardInfoItemDisplay<?>>
			getSearchFactory(
				RenderRequest renderRequest, RenderResponse renderResponse,
				AssetEntryService assetEntryService,
				ContentDashboardInfoItemDisplayMapper
					contentDashboardInfoItemDisplayMapper,
				Portal portal)
		throws PortletException {

		PortletURL portletURL = PortletURLUtil.clone(
			renderResponse.createRenderURL(), renderResponse);

		SearchContainer<ContentDashboardInfoItemDisplay<?>> searchContainer =
			new SearchContainer<>(
				renderRequest, portletURL, null, "no-entries-were-found");

		String orderByCol = ParamUtil.getString(
			renderRequest, "orderByCol", "title");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			renderRequest, "orderByType", "asc");

		searchContainer.setOrderByType(orderByType);

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(renderResponse));

		Collection<String> classNames =
			contentDashboardInfoItemDisplayMapper.getClassNames();

		Stream<String> stream = classNames.stream();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery(
			stream.map(
				portal::getClassNameId
			).mapToLong(
				l -> l
			).toArray(),
			searchContainer);

		assetEntryQuery.setEnablePermissions(true);
		assetEntryQuery.setExcludeZeroViewCount(false);
		assetEntryQuery.setOrderByCol1("title");
		assetEntryQuery.setVisible(Boolean.TRUE);

		assetEntryQuery.setEnd(searchContainer.getEnd());
		assetEntryQuery.setStart(searchContainer.getStart());

		try {
			searchContainer.setResults(
				contentDashboardInfoItemDisplayMapper.map(
					assetEntryService.getEntries(assetEntryQuery)));
			searchContainer.setTotal(
				assetEntryService.getEntriesCount(assetEntryQuery));
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return searchContainer;
	}

}