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

package com.liferay.layout.ptr2322;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.UnicodeProperties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(immediate = true, service = ServiceWrapper.class)
public class LayoutPageTemplateStructureLocalServiceOverride
	extends LayoutPageTemplateStructureLocalServiceWrapper {

	public LayoutPageTemplateStructureLocalServiceOverride() {
		super(null);
	}

	@Override
	public LayoutPageTemplateStructure updateLayoutPageTemplateStructureData(
			long groupId, long plid, long segmentsExperienceId, String data)
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			super.updateLayoutPageTemplateStructureData(
				groupId, plid, segmentsExperienceId, data);

		Layout layout = _layoutLocalService.getLayout(plid);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"segmentsExperienceId", String.valueOf(segmentsExperienceId));

		layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		_layoutLocalService.updateLayout(layout);

		return layoutPageTemplateStructure;
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

}