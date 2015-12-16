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

package com.liferay.portal.convert.database;

import com.liferay.portal.convert.DatabaseConverter;
import com.liferay.portal.convert.util.ModelMigrator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.ClassLoaderUtil;
import com.liferay.portal.kernel.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import javax.sql.DataSource;

/**
 * @author Cristina González
 */
public class PortalDatabaseConverter implements DatabaseConverter {

	@Override
	public void convert(DataSource dataSource) throws Exception {
		List<String> modelClassesNames = _modelMigrator.getModelClassesName(
			"portal", ".*");

		Map<String, Tuple> modelTableDetails = new HashMap<>();

		for (String modelClassesName : modelClassesNames) {
			if (_log.isDebugEnabled()) {
				_log.debug("Loading class " + modelClassesName);
			}

			Class<?> modelClass = getImplClass(modelClassesName);

			if (modelClass == null) {
				_log.error("Unable to load class " + modelClassesName);

				continue;
			}

			modelTableDetails.putAll(
				_modelMigrator.getModelTableDetails(modelClass));
		}

		_modelMigrator.migrate(dataSource, modelTableDetails);
	}

	public void setModelMigrator(ModelMigrator modelMigrator) {
		_modelMigrator = modelMigrator;
	}

	protected Class<?> getImplClass(String implClassName) throws Exception {
		try {
			ClassLoader classLoader = ClassLoaderUtil.getPortalClassLoader();

			return classLoader.loadClass(implClassName);
		}
		catch (Exception e) {
		}

		for (String servletContextName : ServletContextPool.keySet()) {
			try {
				ServletContext servletContext = ServletContextPool.get(
					servletContextName);

				ClassLoader classLoader = servletContext.getClassLoader();

				return classLoader.loadClass(implClassName);
			}
			catch (Exception e) {
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalDatabaseConverter.class);

	private ModelMigrator _modelMigrator;

}