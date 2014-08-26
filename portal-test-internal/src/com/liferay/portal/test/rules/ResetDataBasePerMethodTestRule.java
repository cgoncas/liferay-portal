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

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.Lifecycle;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.SingleVMPoolUtil;
import com.liferay.portal.kernel.cache.ThreadLocalCacheManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.SearchException;
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
public class ResetDataBasePerMethodTestRule implements TestRule {

	public void after() {
		restoreDLStores();
		restoreSearchIndices();

		ResetDatabaseUtil.resetModifiedTables();

		Log4JLoggerTestUtil.setLoggerLevel(Table.class.getName(), _level);

		CacheRegistryUtil.clear();
		SingleVMPoolUtil.clear();
		MultiVMPoolUtil.clear();

		ThreadLocalCacheManager.clearAll(Lifecycle.REQUEST);
	}

	@Override
	public Statement apply(
		final Statement statement, final Description description) {

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					statement.evaluate();
				}
				finally {
					after();
				}
			}

		};
	}

	public void before() {
		_level = Log4JLoggerTestUtil.setLoggerLevel(
			Table.class.getName(), Level.WARN);

		ResetDatabaseUtil.startRecording();

		backupDLStores();
		backupSearchIndices();
	}

	protected void backupDLStores() {
		_dlFileSystemStoreDirName =
			SystemProperties.get(SystemProperties.TMP_DIR) +
				"/temp-dl-file-system-" + System.currentTimeMillis();

		_resetDataBaseRuleUtil.copyDLSStore(
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR,
			_dlFileSystemStoreDirName, false);

		_dlJCRStoreDirName =
			SystemProperties.get(SystemProperties.TMP_DIR) +
				"/temp-dl-jcr-" + System.currentTimeMillis();

		_resetDataBaseRuleUtil.copyDLSStore(
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT),
			_dlJCRStoreDirName, false);
	}

	protected void backupSearchIndices() {
		for (long companyId : PortalInstances.getCompanyIds()) {
			String backupName =
				"temp-search-" + companyId + "-" + System.currentTimeMillis();

			try {
				_resetDataBaseRuleUtil.backupIndexes(
					companyId, backupName, false);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				_indexNames.put(companyId, backupName);
			}
		}
	}

	protected void restoreDLStores() {
		_resetDataBaseRuleUtil.moveDirectory(
			_dlFileSystemStoreDirName,
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR);

		_resetDataBaseRuleUtil.moveDirectory(
			_dlJCRStoreDirName,
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT));
	}

	protected void restoreSearchIndices() {
		for (Map.Entry<Long, String> entry : _indexNames.entrySet()) {
			String backupFileName = entry.getValue();

			try {
				SearchEngineUtil.restore(entry.getKey(), backupFileName);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				try {
					SearchEngineUtil.removeBackup(
						entry.getKey(), backupFileName);
				}
				catch (SearchException e) {
					if (_log.isInfoEnabled()) {
						_log.info("Unable to remove backup", e);
					}
				}
			}
		}

		_indexNames.clear();
	}

	private static Log _log = LogFactoryUtil.getLog(
		ResetDataBasePerMethodTestRule.class);

	private static String _dlFileSystemStoreDirName;
	private static String _dlJCRStoreDirName;
	private static Map<Long, String> _indexNames = new HashMap<Long, String>();
	private static ResetDataBaseRuleUtil _resetDataBaseRuleUtil =
		ResetDataBaseRuleUtil.getInstance();

	private Level _level;

}