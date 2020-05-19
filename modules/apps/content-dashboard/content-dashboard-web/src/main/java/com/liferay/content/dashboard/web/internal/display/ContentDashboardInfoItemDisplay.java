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

package com.liferay.content.dashboard.web.internal.display;

import com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItem;
import com.liferay.portal.kernel.model.User;

import java.util.Date;
import java.util.Locale;

/**
 * @author Cristina González
 */
public class ContentDashboardInfoItemDisplay<T> {

	public static <T> ContentDashboardInfoItemDisplay<T> of(
		ContentDashboardInfoItem<T> contentDashboardInfoItem, T model) {

		return new ContentDashboardInfoItemDisplay<>(
			contentDashboardInfoItem, model);
	}

	public User getAuthor() {
		return _contentDashboardInfoItem.getAuthor(_model);
	}

	public Date getExpiredDate() {
		return _contentDashboardInfoItem.getExpiredDate(_model);
	}

	public Date getModifiedDate() {
		return _contentDashboardInfoItem.getModifiedDate(_model);
	}

	public Date getPublishDate() {
		return _contentDashboardInfoItem.getPublishDate(_model);
	}

	public String getStatusLabel(Locale locale) {
		return _contentDashboardInfoItem.getStatusLabel(_model, locale);
	}

	public String getStatusStyle() {
		return _contentDashboardInfoItem.getStatusStyle(_model);
	}

	public String getTitle(Locale locale) {
		return _contentDashboardInfoItem.getTitle(_model, locale);
	}

	public String getType(Locale locale) {
		return _contentDashboardInfoItem.getType(_model, locale);
	}

	private ContentDashboardInfoItemDisplay(
		ContentDashboardInfoItem<T> contentDashboardInfoItem, T model) {

		_contentDashboardInfoItem = contentDashboardInfoItem;
		_model = model;
	}

	private final ContentDashboardInfoItem<T> _contentDashboardInfoItem;
	private final T _model;

}