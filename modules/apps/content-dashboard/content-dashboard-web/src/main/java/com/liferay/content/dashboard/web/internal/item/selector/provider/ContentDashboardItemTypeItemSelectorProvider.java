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

package com.liferay.content.dashboard.web.internal.item.selector.provider;

import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryTracker;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemType;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemTypeFactory;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemTypeFactoryTracker;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemTypeItemSelectorProvider.class)
public class ContentDashboardItemTypeItemSelectorProvider {

	public SearchContainer<ContentDashboardItemType> getSearchContainer(
		PortletRequest portletRequest, PortletURL portletURL) {

		SearchContainer<ContentDashboardItemType> searchContainer =
			new SearchContainer<>(
				portletRequest, portletURL, null, "there-is-no-subtype");

		searchContainer.setOrderByCol(_getOrderByCol(portletRequest));

		searchContainer.setOrderByType(_getOrderByType(portletRequest));

		SearchResponse searchResponse = _getSearchResponse(
			portletRequest, searchContainer.getEnd(),
			searchContainer.getStart());

		searchContainer.setResults(
			_toContentDashboardItemType(searchResponse.getDocuments71()));

		searchContainer.setTotal(searchResponse.getTotalHits());

		return searchContainer;
	}

	private BooleanClause[] _getBooleanClauses() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		TermsFilter termsFilter = new TermsFilter(Field.CLASS_NAME_ID);

		Collection<Long> classIds =
			_contentDashboardItemFactoryTracker.getClassIds();

		for (Long classId : classIds) {
			termsFilter.addValue(String.valueOf(classId));
		}

		booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);

		booleanQueryImpl.setPreBooleanFilter(booleanFilter);

		return new BooleanClause[] {
			BooleanClauseFactoryUtil.create(
				booleanQueryImpl, BooleanClauseOccur.MUST.getName())
		};
	}

	private String _getKeywords(PortletRequest portletRequest) {
		return ParamUtil.getString(portletRequest, "keywords");
	}

	private String _getOrderByCol(PortletRequest portletRequest) {
		return ParamUtil.getString(
			portletRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM,
			"modified-date");
	}

	private String _getOrderByType(PortletRequest portletRequest) {
		return ParamUtil.getString(
			portletRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM,
			"desc");
	}

	private SearchResponse _getSearchResponse(
		PortletRequest portletRequest, int end, int start) {

		Collection<String> classNames =
			_contentDashboardItemTypeFactoryTracker.getClassNames();

		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).emptySearchEnabled(
				true
			).entryClassNames(
				classNames.toArray(new String[0])
			).fields(
				Field.ENTRY_CLASS_NAME, Field.CLASS_TYPE_ID,
				Field.CLASS_NAME_ID, Field.ENTRY_CLASS_PK, Field.UID
			).highlightEnabled(
				false
			).withSearchContext(
				searchContext -> {
					searchContext.setBooleanClauses(_getBooleanClauses());
					searchContext.setCompanyId(
						_portal.getCompanyId(portletRequest));
					searchContext.setEnd(end);
					searchContext.setKeywords(_getKeywords(portletRequest));
					searchContext.setSorts(_getSort(portletRequest));
					searchContext.setStart(start);
				}
			).build());
	}

	private Sort _getSort(PortletRequest portletRequest) {
		boolean orderByAsc = false;

		String orderByType = _getOrderByType(portletRequest);

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		String orderByCol = _getOrderByCol(portletRequest);

		if (orderByCol.equals("title")) {
			String sortFieldName = Field.getSortableFieldName(
				"localized_name_".concat(
					LocaleUtil.toLanguageId(
						_portal.getLocale(portletRequest))));

			return new Sort(sortFieldName, Sort.STRING_TYPE, !orderByAsc);
		}

		return new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
	}

	private List<ContentDashboardItemType> _toContentDashboardItemType(
		List<Document> documents) {

		Stream<Document> stream = documents.stream();

		return stream.map(
			this::_toContentDashboardItemTypeOptional
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	private Optional<ContentDashboardItemType>
		_toContentDashboardItemTypeOptional(Document document) {

		Optional<ContentDashboardItemTypeFactory>
			contentDashboardItemTypeFactoryOptional =
				_contentDashboardItemTypeFactoryTracker.
					getContentDashboardItemTypeFactoryOptional(
						GetterUtil.getString(
							document.get(Field.ENTRY_CLASS_NAME)));

		return contentDashboardItemTypeFactoryOptional.flatMap(
			contentDashboardItemTypeFactory ->
				_toContentDashboardItemTypeOptional(
					contentDashboardItemTypeFactoryOptional,
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	private Optional<ContentDashboardItemType>
		_toContentDashboardItemTypeOptional(
			Optional<ContentDashboardItemTypeFactory>
				contentDashboardItemTypeFactoryOptional,
			Long classPK) {

		return contentDashboardItemTypeFactoryOptional.flatMap(
			contentDashboardItemTypeFactory -> {
				try {
					return Optional.of(
						contentDashboardItemTypeFactory.create(classPK));
				}
				catch (PortalException portalException) {
					_log.error(portalException, portalException);

					return Optional.<ContentDashboardItemType>empty();
				}
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardItemTypeItemSelectorProvider.class);

	@Reference
	private ContentDashboardItemFactoryTracker
		_contentDashboardItemFactoryTracker;

	@Reference
	private ContentDashboardItemTypeFactoryTracker
		_contentDashboardItemTypeFactoryTracker;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}