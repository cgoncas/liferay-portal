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

package com.liferay.journal.web.internal.upgrade.registry;

import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.upgrade.v1_1_0.MenuFavItemPortalPreferenceValuesUpgradeProcess;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class JournalWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return new String[][] {{"15", JournalPortletKeys.JOURNAL}};
				}

			});

		registry.register(
			"1.0.0", "1.0.1",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.journal.web.configuration.JournalWebConfiguration",
				JournalWebConfiguration.class.getName()));

		registry.register(
			"1.0.1", "1.1.0",
			new MenuFavItemPortalPreferenceValuesUpgradeProcess(
				_classNameLocalService, _ddmStructureLocalService, _portal));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private Portal _portal;

}