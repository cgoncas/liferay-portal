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

	public DLStores backupDLStores(
		String description, boolean deleteFileShutdownHook) {

		DLStores backupDlStores= new DLStores();

		backupDlStores.setDLFileSystemStoreDirName(
			SystemProperties.get(SystemProperties.TMP_DIR) +
				"/temp-"+ description +"-dl-file-system-" +
				System.currentTimeMillis());

		backupDlStores.setDLJCRStoreDirName(
			SystemProperties.get(SystemProperties.TMP_DIR) +
				"/temp-"+ description +"-dl-jcr-" + System.currentTimeMillis());

		copyDLSStore(
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR,
			backupDlStores.getDLFileSystemStoreDirName(),
			deleteFileShutdownHook);

		copyDLSStore(
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT),
			backupDlStores.getDLJCRStoreDirName(),
			deleteFileShutdownHook);

		return backupDlStores;
	}

	public void restoreDLStores(DLStores dlStores) {
		moveDLStore(
			dlStores.getDLFileSystemStoreDirName(),
			PropsValues.DL_STORE_FILE_SYSTEM_ROOT_DIR);

		moveDLStore(
			dlStores.getDLJCRStoreDirName(),
			PropsUtil.get(PropsKeys.JCR_JACKRABBIT_REPOSITORY_ROOT));

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

	public void moveDLStore(
		String originalDLStoreDirName, String targetDLStoreDirName) {

		FileUtil.deltree(targetDLStoreDirName);

		FileUtil.move(
			new File(originalDLStoreDirName), new File(targetDLStoreDirName));
	}

	public class DLStores {

		private String dLFileSystemStoreDirName;

		private String dLJCRStoreDirName;

		public String getDLFileSystemStoreDirName() {
			return dLFileSystemStoreDirName;
		}

		public void setDLFileSystemStoreDirName(String dLFileSystemStoreDirName) {
			this.dLFileSystemStoreDirName = dLFileSystemStoreDirName;
		}

		public String getDLJCRStoreDirName() {
			return dLJCRStoreDirName;
		}

		public void setDLJCRStoreDirName(String dLJCRStoreDirName) {
			this.dLJCRStoreDirName = dLJCRStoreDirName;
		}
	}

}