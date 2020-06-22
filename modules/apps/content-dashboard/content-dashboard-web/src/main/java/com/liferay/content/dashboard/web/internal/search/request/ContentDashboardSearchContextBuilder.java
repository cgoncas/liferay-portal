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

package com.liferay.content.dashboard.web.internal.search.request;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class ContentDashboardSearchContextBuilder {

	public ContentDashboardSearchContextBuilder(
		HttpServletRequest httpServletRequest, long userId) {

		_httpServletRequest = httpServletRequest;
		_userId = userId;
	}

	public SearchContext build() {
		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		int status = GetterUtil.getInteger(
			ParamUtil.getInteger(
				_httpServletRequest, "status", WorkflowConstants.STATUS_ANY));

		searchContext.setAttribute("status", status);

		searchContext.setBooleanClauses(
			Stream.of(
				_getAuthorIdsBooleanClauseOptional(
					ParamUtil.getLongValues(_httpServletRequest, "authorIds")),
				_getUserIdBooleanClauseOptional(_userId)
			).filter(
				Optional::isPresent
			).map(
				Optional::get
			).toArray(
				BooleanClause[]::new
			));

		if (_end != null) {
			searchContext.setEnd(_end);
		}

		long groupId = ParamUtil.getLong(_httpServletRequest, "scopeId");

		if (groupId > 0) {
			searchContext.setGroupIds(new long[] {groupId});
		}
		else {
			searchContext.setGroupIds(null);
		}

		if (_sort != null) {
			searchContext.setSorts(_sort);
		}

		if (_start != null) {
			searchContext.setStart(_start);
		}

		return searchContext;
	}

	public ContentDashboardSearchContextBuilder withEnd(int end) {
		_end = end;

		return this;
	}

	public ContentDashboardSearchContextBuilder withSort(Sort sort) {
		_sort = sort;

		return this;
	}

	public ContentDashboardSearchContextBuilder withStart(int start) {
		_start = start;

		return this;
	}

	private BooleanFilter _getApprovedContentBooleanFilter() {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.addRequiredTerm("headListable", true);

		return booleanFilter;
	}

	private Optional<BooleanClause<Query>> _getAuthorIdsBooleanClauseOptional(
		long[] authorIds) {

		if (ArrayUtil.isEmpty(authorIds)) {
			return Optional.empty();
		}

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		TermsFilter termsFilter = new TermsFilter(Field.USER_ID);

		for (long authorId : authorIds) {
			termsFilter.addValue(String.valueOf(authorId));
		}

		booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);

		booleanQuery.setPreBooleanFilter(booleanFilter);

		return Optional.of(
			BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName()));
	}

	private BooleanFilter _getMyContentBooleanFilter(long userId) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.addRequiredTerm("headListable", false);
		booleanFilter.addRequiredTerm("latest", true);
		booleanFilter.addRequiredTerm("userId", userId);

		return booleanFilter;
	}

	private Optional<BooleanClause<Query>> _getUserIdBooleanClauseOptional(
		long userId) {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(
			_getApprovedContentBooleanFilter(), BooleanClauseOccur.SHOULD);
		booleanFilter.add(
			_getMyContentBooleanFilter(userId), BooleanClauseOccur.SHOULD);

		booleanQuery.setPreBooleanFilter(booleanFilter);

		return Optional.of(
			BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName()));
	}

	private Integer _end;
	private final HttpServletRequest _httpServletRequest;
	private Sort _sort;
	private Integer _start;
	private final long _userId;

}