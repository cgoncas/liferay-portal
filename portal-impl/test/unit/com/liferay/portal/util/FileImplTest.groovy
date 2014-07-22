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

package com.liferay.portal.util;

import com.liferay.portal.kernel.util.StringPool;

import spock.lang.Specification;

/**
 * @author Brian Wing Shun Chan
 * @author Cristina González
 */
public class FileImplTest extends Specification {

	def "when the fullFileName doesn't have any slash, the path should be the forward slash" () {
		expect:
			String path = _fileImpl.getPath("aaa.bbb");

			path.equals(StringPool.SLASH);
	}

	def "when the fullFileName doesn't have any slash, the shortFileName should be the fullFileName" () {
		expect:
			String shortFileName = _fileImpl.getShortFileName("aaa.bbb");

			shortFileName.equals("aaa.bbb");
	}

	def "when the last slash of the fullFileName is a back slash, the path should be the string before that slash" () {
		expect:
			String path = _fileImpl.getPath(actualFullFileName);

			path.equals(expectedPath);

		where:
			actualFullFileName | expectedPath
			"aaa/bbb\\ccc/ddd\\eee.fff" | "aaa/bbb\\ccc/ddd"
			"aaa\\bbb\\ccc\\ddd\\eee.fff" | "aaa\\bbb\\ccc\\ddd"
			"aaa/bbb/ccc/ddd\\eee.fff" | "aaa/bbb/ccc/ddd"
	}

	def "when the last slash of the fullFileName is a back slash, the shortFileName should be the string after that slash" () {
		expect:
			String shortFileName = _fileImpl.getShortFileName(
				actualFullFileName);

			shortFileName.equals(expectedShortFileName);

		where:
			actualFullFileName | expectedShortFileName
			"aaa\\bbb/ccc\\ddd\\eee.fff" | "eee.fff"
			"aaa/bbb/ccc/ddd\\eee.fff" | "eee.fff"
			"aaa\\bbb\\ccc\\ddd\\eee.fff" |"eee.fff"
	}

	def "when the last slash of the fullFileName is a forward slash, the path should be the string before that slash" () {
		expect:
			String path = _fileImpl.getPath(actualFullFileName);

			path.equals(expectedPath);

		where:
			actualFullFileName | expectedPath
			"aaa\\bbb/ccc\\ddd/eee.fff" | "aaa\\bbb/ccc\\ddd"
			"aaa/bbb/ccc/ddd/eee.fff" | "aaa/bbb/ccc/ddd"
			"aaa\\bbb\\ccc\\ddd/eee.fff" |"aaa\\bbb\\ccc\\ddd"
	}

	def "when the last slash of the fullFileName is a forward slash, the shortFileName should be the string after that slash" () {
		expect:
			String shortFileName = _fileImpl.getShortFileName(
				actualFullFileName);

			shortFileName.equals(expectedShortFileName);

		where:
			actualFullFileName | expectedShortFileName
			"aaa\\bbb/ccc\\ddd/eee.fff" | "eee.fff"
			"aaa/bbb/ccc/ddd/eee.fff" | "eee.fff"
			"aaa\\bbb\\ccc\\ddd/eee.fff" |"eee.fff"
	}

	private FileImpl _fileImpl = new FileImpl();

}