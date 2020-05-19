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

package com.liferay.content.dashboard.web.internal.info.item;

import com.liferay.portal.kernel.model.User;

import java.util.Date;
import java.util.Locale;

/**
 * @author Cristina González
 */
public interface ContentDashboardInfoItem<T> {

	public User getAuthor(T model);

	public Date getExpiredDate(T model);

	public Date getModifiedDate(T model);

	public Date getPublishDate(T model);

	public String getStatusLabel(T model, Locale locale);

	public String getStatusStyle(T model);

	public String getTitle(T model, Locale locale);

	public String getType(T model, Locale locale);

}