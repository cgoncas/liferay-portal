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

package com.liferay.asset.categories.admin.web.internal.info.item.provider;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = InfoItemPermissionProvider.class)
public class AssetCategoryInfoItemPermissionProvider
	implements InfoItemPermissionProvider<AssetCategory> {

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, AssetCategory assetCategory,
			String actionId)
		throws InfoItemPermissionException {

		return _hasPermission(
			permissionChecker, assetCategory.getCategoryId(), actionId);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		return _hasPermission(
			permissionChecker, classPKInfoItemIdentifier.getClassPK(),
			actionId);
	}

	private boolean _hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws InfoItemPermissionException {

		try {
			return _assetCategoryModelResourcePermission.contains(
				permissionChecker, classPK, actionId);
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(classPK, portalException);
		}
	}

	@Reference(
		target = "(model.class.name=com.liferay.asset.kernel.model.AssetCategory)"
	)
	private ModelResourcePermission<AssetCategory>
		_assetCategoryModelResourcePermission;

}