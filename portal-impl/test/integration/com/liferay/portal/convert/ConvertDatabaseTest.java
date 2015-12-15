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

package com.liferay.portal.convert;

import com.liferay.portal.convert.bundle.customconvertdatabaseprocess.TestCustomConvertDatabaseProcess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.MainServletTestRule;
import com.liferay.portal.test.rule.SyntheticBundleRule;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class ConvertDatabaseTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), MainServletTestRule.INSTANCE,
			new SyntheticBundleRule("bundle.customconvertdatabaseprocess"));

	@Test
	public void testGetConvertDatabase() {
		ConvertDatabase convertDatabase = _getConvertDatabase();

		Assert.assertNotNull(
			"The ConvertDatabase is NOT correctly registered", convertDatabase);
	}

	@Test
	public void testGetCustomConvertDatabaseProcess() {
		ConvertDatabase convertDatabase = _getConvertDatabase();

		ConvertDatabaseProcess convertDatabaseProcess =
			_getFirstConvertDataBaseProcess(
				convertDatabase, TestCustomConvertDatabaseProcess.class);

		Assert.assertNotNull(
			"The TestCustomConvertDatabaseProcess has not being registered " +
				"as a database converter",
			convertDatabaseProcess);
	}

	@Test
	public void testGetPortalConvertDatabaseProcess() {
		ConvertDatabase convertDatabase = _getConvertDatabase();

		ConvertDatabaseProcess portalConvertDatabaseProcess =
			_getFirstConvertDataBaseProcess(
				convertDatabase, PortalConvertDatabaseProcess.class);

		Assert.assertNotNull(
			"The PortalConvertDatabaseProcess has not being registered " +
				"as a database converter",
			portalConvertDatabaseProcess);
	}

	private ConvertDatabase _getConvertDatabase() {
		Collection<ConvertProcess> enabledConvertProcesses =
			ConvertProcessUtil.getEnabledConvertProcesses();

		for (ConvertProcess enabledConvertProcess : enabledConvertProcesses) {
			if (enabledConvertProcess instanceof ConvertDatabase) {
				return (ConvertDatabase)enabledConvertProcess;
			}
		}

		return null;
	}

	private ConvertDatabaseProcess _getFirstConvertDataBaseProcess(
		ConvertDatabase convertDatabase, Class<?> convertDataProcessClass) {

		for (ConvertDatabaseProcess convertDatabaseProcess :
				convertDatabase.getConvertDatabaseProcesses()) {

			if (convertDatabaseProcess.getClass().getName().equals(
					convertDataProcessClass.getName())) {

				return convertDatabaseProcess;
			}
		}

		return null;
	}

}