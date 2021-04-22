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

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceWrapper;
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
public class FragmentEntryLinkLocalServiceOverride
	extends FragmentEntryLinkLocalServiceWrapper {

	public FragmentEntryLinkLocalServiceOverride() {
		super(null);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink = super.updateFragmentEntryLink(
			fragmentEntryLinkId, editableValues, updateClassedModel);

		Layout layout = _layoutLocalService.getLayout(
			fragmentEntryLink.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"segmentsExperienceId",
			String.valueOf(fragmentEntryLink.getSegmentsExperienceId()));

		layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		_layoutLocalService.updateLayout(layout);

		return fragmentEntryLink;
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

}