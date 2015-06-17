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

package com.liferay.portlet.trash.test;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.trash.service.TrashEntryLocalServiceUtil;

/**
 * @author Cristina González
 */
public class DefaultWhenIsIndexableBaseModel
	implements WhenIsIndexableBaseModel {

	public String getSearchKeywords() {
		return "Title";
	}

	public int searchTrashEntriesCount(
			String keywords, ServiceContext serviceContext)
		throws Exception {

		Hits results = TrashEntryLocalServiceUtil.search(
			serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
			serviceContext.getUserId(), keywords, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return results.getLength();
	}

}