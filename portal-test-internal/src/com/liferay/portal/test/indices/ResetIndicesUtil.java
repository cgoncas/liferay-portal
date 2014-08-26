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

package com.liferay.portal.test.indices;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.test.file.DeleteFileShutdownHook;
import com.liferay.portal.util.PortalInstances;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shuyang Zhou
 * @author Cristina González
 */
public class ResetIndicesUtil {

	public static ResetIndicesUtil getInstance() {
		return new ResetIndicesUtil();
	}

	public  Map<Long, String> backupSearchIndices(
		String description, boolean deleteFileShutdownHook ) {

		Map<Long, String> backupFileNames = new HashMap<Long, String>() ;

		for (long companyId : PortalInstances.getCompanyIds()) {
			String backupName =
					"temp-" + description + "-search-" + companyId + "-" +
						System.currentTimeMillis();
			try {
				String backupFileName = SearchEngineUtil.backup(
					companyId, SearchEngineUtil.SYSTEM_ENGINE_ID, backupName);

				if (deleteFileShutdownHook) {
					Runtime runtime = Runtime.getRuntime();

					runtime.addShutdownHook(
						new DeleteFileShutdownHook(backupFileName));
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				backupFileNames.put(companyId, backupName);
			}
		}

		return backupFileNames;
	}

	public void restoreSearchIndices(
		Map<Long, String> backupFileNames, boolean removeBackup) {
		for (Map.Entry<Long, String> entry : backupFileNames.entrySet()) {
			String backupFileName = entry.getValue();

			try {
				SearchEngineUtil.restore(entry.getKey(), backupFileName);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				if (removeBackup) {
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
		}

		if (removeBackup) {
			backupFileNames.clear();
		}
	}

	private static Log _log = LogFactoryUtil.getLog(ResetIndicesUtil.class);

}