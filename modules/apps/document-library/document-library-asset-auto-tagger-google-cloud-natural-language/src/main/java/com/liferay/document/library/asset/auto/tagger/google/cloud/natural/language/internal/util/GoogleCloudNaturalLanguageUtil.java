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

package com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alicia García
 */
public class GoogleCloudNaturalLanguageUtil {

	public static List<String> splitTextToMaxSizeCall(
		String contentText, int max) {
		if (Validator.isNull(contentText)) {
			return Collections.emptyList();
		}
		List<String> matchList = new ArrayList<>();

		int jsonSkeletonSize = _getAnnotateDocumentPayload("").length();

		if (contentText.getBytes().length +
			jsonSkeletonSize < max) {
			matchList.add(
				_getAnnotateDocumentPayload(contentText));
		}
		else {


			String[] lines = contentText.split("\\r?\\n", max);
			StringBuffer linesAccumulator = new StringBuffer();
			int spaceLength = StringPool.SPACE.getBytes().length;

			for (int i = 0; i < lines.length; i++) {
				if (linesAccumulator.toString().getBytes().length +
					jsonSkeletonSize + lines[i].getBytes().length +
					spaceLength > max) {
					matchList.add(
						_getAnnotateDocumentPayload(
							linesAccumulator.toString()));
					linesAccumulator = new StringBuffer();
				}

				linesAccumulator.append(lines[i] + StringPool.SPACE);
			}
			matchList.add(
				_getAnnotateDocumentPayload(linesAccumulator.toString()));

		}
		return matchList;
	}

	private static String _getAnnotateDocumentPayload(String content) {
		JSONObject jsonObject = JSONUtil.put(
			"document",
			JSONUtil.put(
				"type", "PLAIN_TEXT"
			).put(
				"content", content
			));

		return jsonObject.toString();
	}

}