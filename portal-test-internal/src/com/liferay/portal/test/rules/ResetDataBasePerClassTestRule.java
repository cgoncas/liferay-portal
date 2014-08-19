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

import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.log.Log4JLoggerTestUtil;
import com.liferay.portal.test.jdbc.ResetDatabaseUtil;
import com.liferay.portal.upgrade.util.Table;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Shuyang Zhou
 * @author Cristina González
 */
public class ResetDataBasePerClassTestRule implements TestRule {

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

	public void before() {
		_level = Log4JLoggerTestUtil.setLoggerLevel(
			Table.class.getName(), Level.WARN);

		try {
			if (ResetDatabaseUtil.initialize()) {
				backupDLStores();
				backupSearchIndices();
			}
			else {
				restoreDLStores();
				restoreSearchIndices();
			}
		}
		finally {
			Log4JLoggerTestUtil.setLoggerLevel(Table.class.getName(), _level);
		}
	}

	protected void backupDLStores() {
		_initializedDLFileSystemStoreDirName =
			SystemProperties.get(SystemProperties.TMP_DIR) +
				"/temp-init-dl-file-system-" + System.currentTimeMillis();

		_resetDataBaseRuleUtil.copyDLSStore(
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR,
			_initializedDLFileSystemStoreDirName, true);

		_initializedDLJCRStoreDirName =
			SystemProperties.get(SystemProperties.TMP_DIR) +
				"/temp-init-dl-jcr-" + System.currentTimeMillis();

		_resetDataBaseRuleUtil.copyDLSStore(
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT),
			_initializedDLJCRStoreDirName, true);
	}

	protected void backupSearchIndices() {
		for (long companyId : PortalInstances.getCompanyIds()) {
			String backupName =
				"temp-init-search-" + companyId + "-" +
					System.currentTimeMillis();

			try {
				_resetDataBaseRuleUtil.backupIndexes(
					companyId, backupName, true);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				_initializedIndexNames.put(companyId, backupName);
			}
		}
	}

	protected void restoreDLStores() {
		_resetDataBaseRuleUtil.moveDirectory(
			_initializedDLFileSystemStoreDirName,
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR);

		_resetDataBaseRuleUtil.moveDirectory(
			_initializedDLJCRStoreDirName,
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT));
	}

	protected void restoreSearchIndices() {
		for (Map.Entry<Long, String> entry :_initializedIndexNames.entrySet()) {
			String backupFileName = entry.getValue();

			try {
				SearchEngineUtil.restore(entry.getKey(), backupFileName);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static String _initializedDLFileSystemStoreDirName;
	private static String _initializedDLJCRStoreDirName;
	private static Map<Long, String> _initializedIndexNames =
		new HashMap<Long, String>();
	private static ResetDataBaseRuleUtil _resetDataBaseRuleUtil =
		ResetDataBaseRuleUtil.getInstance();

	private Level _level;

}