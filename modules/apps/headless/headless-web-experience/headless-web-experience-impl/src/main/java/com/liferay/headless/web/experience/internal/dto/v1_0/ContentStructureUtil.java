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

package com.liferay.headless.web.experience.internal.dto.v1_0;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.headless.web.experience.dto.v1_0.ContentStructure;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;

/**
 * @author Cristina González
 */
public class ContentStructureUtil {

	public static ContentStructure toContentStructure(
			DDMStructure ddmStructure, Locale locale, Portal portal,
			UserLocalService userLocalService)
		throws Exception {

		if (ddmStructure == null) {
			return null;
		}

		return new ContentStructure() {
			{
				setAvailableLanguages(
					LocaleUtil.toW3cLanguageIds(
						ddmStructure.getAvailableLanguageIds()));
				setContentSpace(ddmStructure.getGroupId());
				setCreator(
					CreatorUtil.toCreator(
						portal,
						userLocalService.getUserById(
							ddmStructure.getUserId())));
				setDateCreated(ddmStructure.getCreateDate());
				setDateModified(ddmStructure.getModifiedDate());
				setDescription(ddmStructure.getDescription(locale));
				setId(ddmStructure.getStructureId());
				setName(ddmStructure.getName(locale));
			}
		};
	}

}