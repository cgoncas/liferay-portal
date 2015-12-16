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

package com.liferay.portal.convert.util;

import com.liferay.portal.kernel.util.Tuple;

import java.io.IOException;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author Cristina González
 */
public interface ModelMigrator {

	public List<String> getModelClassesName(String moduleName, String pattern);

	public Map<String, Tuple> getModelTableDetails(Class<?> implClass);

	public void migrate(
			DataSource dataSource, Map<String, Tuple> modelTableDetails)
		throws IOException, SQLException;

}