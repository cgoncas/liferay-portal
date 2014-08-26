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
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Cristina González
 */
public class ResetDataBaseRuleUtil {

	public static ResetDataBaseRuleUtil getInstance() {
		return new ResetDataBaseRuleUtil();
	}

	public void backupIndexes(
			long companyId, String backupName, boolean deleteFileShutdownHook)
		throws SearchException {

			String backupFileName = SearchEngineUtil.backup(
				companyId, SearchEngineUtil.SYSTEM_ENGINE_ID, backupName);

			if (deleteFileShutdownHook) {
				Runtime runtime = Runtime.getRuntime();

				runtime.addShutdownHook(
					new DeleteFileShutdownHook(backupFileName));
			}
	}

	public void copyDirectory(String originDirName, String targetDirName) {
		try {
			FileUtil.copyDirectory(
				new File(originDirName), new File(targetDirName));
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public void copyDLSStore(
		String originalFileSystemStoreDirName,
		String targetFileSystemStoreDirName,
		boolean deleteFileShutdownHook) {

		if (deleteFileShutdownHook) {
			Runtime runtime = Runtime.getRuntime();

			runtime.addShutdownHook(
				new DeleteFileShutdownHook(targetFileSystemStoreDirName));
		}

		copyDirectory(
			originalFileSystemStoreDirName, targetFileSystemStoreDirName);
	}

	public void moveDirectory(String originDirName, String targetDirName) {
		FileUtil.deltree(targetDirName);

		FileUtil.move(new File(originDirName), new File(targetDirName));
	}

	private class DeleteFileShutdownHook extends Thread {

		public DeleteFileShutdownHook(String fileName) {
			_fileName = fileName;
		}

		@Override
		public void run() {
			File file = new File(_fileName);

			Queue<File> queue = new LinkedList<File>();

			queue.offer(file);

			while ((file = queue.poll()) != null) {
				if (file.isFile()) {
					file.delete();
				}
				else if (file.isDirectory()) {
					File[] files = file.listFiles();

					if (files.length == 0) {
						file.delete();
					}
					else {
						queue.addAll(Arrays.asList(files));
						queue.add(file);
					}
				}
			}
		}

		private String _fileName;

	}

}