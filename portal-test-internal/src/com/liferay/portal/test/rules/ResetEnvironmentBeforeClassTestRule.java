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

package com.liferay.portal.test.rules;

import com.liferay.portal.log.Log4JLoggerTestUtil;
import com.liferay.portal.test.documentlibrary.ResetDocumentLibraryUtil;
import com.liferay.portal.test.indices.ResetIndicesUtil;
import com.liferay.portal.test.jdbc.ResetDatabaseUtil;
import com.liferay.portal.upgrade.util.Table;
import org.apache.log4j.Level;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cristina González
 */
public class ResetEnvironmentBeforeClassTestRule implements TestRule {

	@Override
	public Statement apply(
		final Statement statement, final Description description) {

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				before();
				statement.evaluate();
			}

		};
	}

	private void before() {
		_level = Log4JLoggerTestUtil.setLoggerLevel(
			Table.class.getName(), Level.WARN);

		try {
			if (!_initialized) {
				ResetDatabaseUtil.dumpDatabase();

				_initializedDLStores = _resetDocumentLibraryUtil.backupDLStores(
					"init", true);

				_initializedIndexNames = _resetIndicesUtil.backupSearchIndices(
					"init", true);

				_initialized = true;
			}
			else {
				ResetDatabaseUtil.reloadDatabase();
				_resetDocumentLibraryUtil.restoreDLStores(_initializedDLStores);
				_resetIndicesUtil.restoreSearchIndices(_initializedIndexNames);
			}
		}
		finally {
			Log4JLoggerTestUtil.setLoggerLevel(Table.class.getName(), _level);
		}
	}

	private static ResetDocumentLibraryUtil.DLStores _initializedDLStores;
	private static boolean _initialized;
	private static Map<Long, String> _initializedIndexNames =
		new HashMap<Long, String>();
	private static ResetDocumentLibraryUtil _resetDocumentLibraryUtil =
		ResetDocumentLibraryUtil.getInstance();
	private static ResetIndicesUtil _resetIndicesUtil =
		ResetIndicesUtil.getInstance();



	private Level _level;



}