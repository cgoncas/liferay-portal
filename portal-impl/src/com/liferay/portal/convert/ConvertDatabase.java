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

import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.Collection;

import javax.sql.DataSource;

/**
 * @author Alexander Chow
 */
public class ConvertDatabase extends BaseConvertProcess {

	@Override
	public String getDescription() {
		return "migrate-data-from-one-database-to-another";
	}

	@Override
	public String getParameterDescription() {
		return "please-enter-jdbc-information-for-new-database";
	}

	@Override
	public String[] getParameterNames() {
		return new String[] {
			"jdbc-driver-class-name", "jdbc-url", "jdbc-user-name",
			"jdbc-password"
		};
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected void doConvert() throws Exception {
		Collection<ConvertDatabaseProcess> convertDatabaseProcesses =
			getConvertDatabaseProcesses();

		for (ConvertDatabaseProcess convertDatabaseProcess :
				convertDatabaseProcesses) {

			convertDatabaseProcess.convert(getDataSource());
		}
	}

	protected Collection<ConvertDatabaseProcess> getConvertDatabaseProcesses() {
		try {
			Registry registry = RegistryUtil.getRegistry();

			return registry.getServices(ConvertDatabaseProcess.class, null);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	protected DataSource getDataSource() throws Exception {
		String[] values = getParameterValues();

		String driverClassName = values[0];
		String url = values[1];
		String userName = values[2];
		String password = values[3];
		String jndiName = StringPool.BLANK;

		return DataSourceFactoryUtil.initDataSource(
			driverClassName, url, userName, password, jndiName);
	}

}