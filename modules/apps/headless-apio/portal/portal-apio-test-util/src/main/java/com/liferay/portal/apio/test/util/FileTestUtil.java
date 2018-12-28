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

package com.liferay.portal.apio.test.util;

import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import java.net.URL;

import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
public class FileTestUtil {

	public static File getFile(String fileName, Class<?> clazz) {
		URL url = clazz.getResource(fileName);

		return new File(url.getFile());
	}

	public static String readFile(String fileName, Class<?> clazz)
		throws Exception {

		return readFile(fileName, clazz, Collections.emptyList());
	}

	public static String readFile(
			String fileName, Class<?> clazz, List<String> vars)
		throws Exception {

		URL url = clazz.getResource(fileName);

		return String.format(StringUtil.read(url.openStream()), vars.toArray());
	}

}