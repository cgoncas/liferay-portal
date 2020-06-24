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

package com.liferay.content.dashboard.web.internal.display.context;

import com.liferay.content.dashboard.web.internal.configuration.ContentDashboardConfiguration;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.content.dashboard.web.internal.servlet.taglib.util.ContentDashboardDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.users.admin.item.selector.UserItemSelectorCriterion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletURL;

/**
 * @author Cristina González
 */
public class ContentDashboardAdminDisplayContext {

	public ContentDashboardAdminDisplayContext(
		ContentDashboardConfiguration contentDashboardConfiguration,
		ContentDashboardDropdownItemsProvider
			contentDashboardDropdownItemsProvider,
		ItemSelector itemSelector, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, Portal portal,
		SearchContainer<ContentDashboardItem<?>> searchContainer) {

		_contentDashboardConfiguration = contentDashboardConfiguration;
		_contentDashboardDropdownItemsProvider =
			contentDashboardDropdownItemsProvider;
		_itemSelector = itemSelector;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portal = portal;
		_searchContainer = searchContainer;
	}

	public List<Long> getAuthorIds() {
		if (_authorIds != null) {
			return _authorIds;
		}

		_authorIds = Arrays.asList(
			ArrayUtil.toLongArray(
				ParamUtil.getLongValues(_liferayPortletRequest, "authorIds")));

		return _authorIds;
	}

	public String getAuthorItemSelectorEventName() {
		return _liferayPortletResponse.getNamespace() + "selectedAuthorItem";
	}

	public String getAuthorItemSelectorURL() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest);

		UserItemSelectorCriterion userItemSelectorCriterion =
			new UserItemSelectorCriterion();

		userItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(new UUIDItemSelectorReturnType()));

		PortletURL portletURL = _itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, getAuthorItemSelectorEventName(),
			userItemSelectorCriterion);

		portletURL.setParameter(
			"checkedUserIds", StringUtil.merge(getAuthorIds()));
		portletURL.setParameter(
			"checkedUserIdsEnabled", String.valueOf(Boolean.TRUE));

		return portletURL.toString();
	}

	public List<DropdownItem> getDropdownItems(
		ContentDashboardItem contentDashboardItem) {

		return _contentDashboardDropdownItemsProvider.getDropdownItems(
			contentDashboardItem);
	}

	public long getScopeId() {
		if (_scopeId > 0) {
			return _scopeId;
		}

		_scopeId = ParamUtil.getLong(_liferayPortletRequest, "scopeId");

		return _scopeId;
	}

	public String getScopeIdItemSelectorURL() throws PortalException {
		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());
		groupItemSelectorCriterion.setIncludeAllVisibleGroups(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				_liferayPortletResponse.getNamespace() + "selectedScopeIdItem",
				groupItemSelectorCriterion));
	}

	public SearchContainer<ContentDashboardItem<?>> getSearchContainer() {
		return _searchContainer;
	}

	public Integer getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			_liferayPortletRequest, "status", WorkflowConstants.STATUS_ANY);

		return _status;
	}

	public long getUserId() {
		if (_userId > 0) {
			return _userId;
		}

		_userId = _portal.getUserId(_liferayPortletRequest);

		return _userId;
	}

	public boolean isAuditGraphEnabled() {
		return _contentDashboardConfiguration.auditGraphEnabled();
	}

	private List<Long> _authorIds;
	private final ContentDashboardConfiguration _contentDashboardConfiguration;
	private final ContentDashboardDropdownItemsProvider
		_contentDashboardDropdownItemsProvider;
	private final ItemSelector _itemSelector;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final Portal _portal;
	private long _scopeId;
	private final SearchContainer<ContentDashboardItem<?>> _searchContainer;
	private Integer _status;
	private long _userId;

}