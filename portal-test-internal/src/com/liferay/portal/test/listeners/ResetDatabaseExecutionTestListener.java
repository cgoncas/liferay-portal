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

package com.liferay.portal.test.listeners;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.Lifecycle;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.SingleVMPoolUtil;
import com.liferay.portal.kernel.cache.ThreadLocalCacheManager;
import com.liferay.portal.kernel.test.AbstractExecutionTestListener;
import com.liferay.portal.kernel.test.TestContext;
import com.liferay.portal.log.Log4JLoggerTestUtil;
import com.liferay.portal.test.documentlibrary.ResetDocumentLibraryUtil;
import com.liferay.portal.test.indices.ResetIndicesUtil;
import com.liferay.portal.test.jdbc.ResetDatabaseUtil;
import com.liferay.portal.upgrade.util.Table;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;

/**
 * @author Shuyang Zhou
 */
public class ResetDatabaseExecutionTestListener
	extends AbstractExecutionTestListener {

	@Override
	public void runAfterTest(TestContext testContext) {
		_resetDocumentLibraryUtil.restoreDLStores(_dlStores);
		_resetIndicesUtil.restoreSearchIndices(_indexNames);
		_resetIndicesUtil.clearSearchIndices(_indexNames);

		ResetDatabaseUtil.resetModifiedTables();

		Log4JLoggerTestUtil.setLoggerLevel(Table.class.getName(), _level);

		CacheRegistryUtil.clear();
		SingleVMPoolUtil.clear();
		MultiVMPoolUtil.clear();

		ThreadLocalCacheManager.clearAll(Lifecycle.REQUEST);
	}

	@Override
	public void runBeforeClass(TestContext testContext) {
		_level = Log4JLoggerTestUtil.setLoggerLevel(
			Table.class.getName(), Level.WARN);

		try {
			if (ResetDatabaseUtil.initialize()) {
				_initDLStores = _resetDocumentLibraryUtil.backupDLStores(
					"init", true);

				_initializedIndexNames = _resetIndicesUtil.backupSearchIndices(
					"init", true);
			}
			else {
				_resetDocumentLibraryUtil.restoreDLStores(_initDLStores);
				_resetIndicesUtil.restoreSearchIndices(_initializedIndexNames);
			}
		}
		finally {
			Log4JLoggerTestUtil.setLoggerLevel(Table.class.getName(), _level);
		}
	}

	@Override
	public void runBeforeTest(TestContext testContext) {
		_level = Log4JLoggerTestUtil.setLoggerLevel(
			Table.class.getName(), Level.WARN);

		ResetDatabaseUtil.startRecording();

		_dlStores = _resetDocumentLibraryUtil.backupDLStores("class", false);
		_indexNames = _resetIndicesUtil.backupSearchIndices("class", false);
	}

	private static ResetDocumentLibraryUtil.DLStores _initDLStores;
	private static Map<Long, String> _initializedIndexNames =
		new HashMap<Long, String>();
	private static ResetDocumentLibraryUtil _resetDocumentLibraryUtil =
		ResetDocumentLibraryUtil.getInstance();
	private static ResetIndicesUtil _resetIndicesUtil =
		ResetIndicesUtil.getInstance();

	private ResetDocumentLibraryUtil.DLStores _dlStores;
	private Map<Long, String> _indexNames = new HashMap<Long, String>();
	private Level _level;

}