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

package com.liferay.portal.test.documentlibrary;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.file.DeleteFileShutdownHook;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;

/**
 * @author Shuyang Zhou
 * @author Cristina González
 */
public class ResetDocumentLibraryUtil {

	public static ResetDocumentLibraryUtil getInstance() {
		return new ResetDocumentLibraryUtil();
	}

	public void backupDLStores(boolean initialize) {
		String dlFileSystemStoreDirName = null;

		if (initialize) {
			dlFileSystemStoreDirName =
				SystemProperties.get(SystemProperties.TMP_DIR) +
					"/temp-init-dl-file-system-" + System.currentTimeMillis();

			_initializedDLFileSystemStoreDirName = dlFileSystemStoreDirName;
		}
		else {
			dlFileSystemStoreDirName =
				SystemProperties.get(SystemProperties.TMP_DIR) +
					"/temp-dl-file-system-" + System.currentTimeMillis();

			_dlFileSystemStoreDirName = dlFileSystemStoreDirName;
		}

		copyDLSStore(
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR, dlFileSystemStoreDirName,
			initialize);

		String dlJCRStoreDirName = null;

		if (initialize) {
			dlJCRStoreDirName =
				SystemProperties.get(SystemProperties.TMP_DIR) +
					"/temp-init-dl-jcr-" + System.currentTimeMillis();

			_initializedDLJCRStoreDirName = dlJCRStoreDirName;
		}
		else {
			dlJCRStoreDirName =
				SystemProperties.get(SystemProperties.TMP_DIR) +
					"/temp-dl-jcr-" + System.currentTimeMillis();

			_dlJCRStoreDirName = dlJCRStoreDirName;
		}

		copyDLSStore(
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT),
			dlJCRStoreDirName, initialize);

	}

	public void restoreDLStores(boolean initialize) {
		FileUtil.deltree(PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR);

		String dlFileSystemStoreDirName = _initializedDLFileSystemStoreDirName;

		if (!initialize) {
			dlFileSystemStoreDirName = _dlFileSystemStoreDirName;

			_dlFileSystemStoreDirName = null;
		}

		FileUtil.move(
			new File(dlFileSystemStoreDirName),
			new File(PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR));

		FileUtil.deltree(
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT));

		String dlJCRStoreDirName = _initializedDLJCRStoreDirName;

		if (!initialize) {
			dlJCRStoreDirName = _dlJCRStoreDirName;

			_dlJCRStoreDirName = null;
		}

		FileUtil.move(
			new File(dlJCRStoreDirName),
			new File(PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT)));
	}

	protected void copyDLSStore(
		String originalDLStoreDirName, String targetDLStoreDirName,
		boolean deleteFileShutdownHook) {

		if (deleteFileShutdownHook) {
			Runtime runtime = Runtime.getRuntime();

			runtime.addShutdownHook(
				new DeleteFileShutdownHook(targetDLStoreDirName));
		}

		try {
			FileUtil.copyDirectory(
				new File(originalDLStoreDirName),
				new File(targetDLStoreDirName));
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private static String _initializedDLFileSystemStoreDirName;
	private static String _initializedDLJCRStoreDirName;

	private String _dlFileSystemStoreDirName;
	private String _dlJCRStoreDirName;

}