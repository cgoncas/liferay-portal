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

package com.liferay.dynamic.data.mapping.upgrade.v1_0_0;

import com.liferay.dynamic.data.mapping.upgrade.v1_0_0.util.companyId.DDMStorageLinkUpgradeCompanyId;
import com.liferay.dynamic.data.mapping.upgrade.v1_0_0.util.companyId.DDMStructureLinkUpgradeCompanyId;
import com.liferay.portal.kernel.upgrade.util.UpgradeCompanyIdInTable;

/**
 * @author Brian Wing Shun Chan
 */
public class UpgradeCompanyId
	extends com.liferay.portal.upgrade.util.UpgradeCompanyId {

	@Override
	protected UpgradeCompanyIdInTable[] getUpgradeCompanyIdInTable() {
		return new UpgradeCompanyIdInTable[] {
			new DDMStorageLinkUpgradeCompanyId(),
			new DDMStructureLinkUpgradeCompanyId()
		};
	}

}