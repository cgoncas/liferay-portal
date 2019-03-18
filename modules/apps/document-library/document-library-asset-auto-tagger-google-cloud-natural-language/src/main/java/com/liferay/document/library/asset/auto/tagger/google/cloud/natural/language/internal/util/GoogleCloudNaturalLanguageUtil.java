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
		String contentText, int max, String textType) {

		if (Validator.isNull(contentText)) {
			return Collections.emptyList();
		}

		List<String> matchList = new ArrayList<>();

		int jsonSkeletonSize = _getAnnotateDocumentPayload(
			"", textType
		).length();

		byte[] contentSize = contentText.getBytes();

		if (contentSize.length + jsonSkeletonSize < max) {
			matchList.add(_getAnnotateDocumentPayload(contentText, textType));
		}
		else {
			String[] units;
			if (contentText.contains("\n") || contentText.contains("\r")) {
				units = contentText.split("\\r?\\n", max);
			}
			else {
				units = contentText.split("\\s?$", max);
			}
			_accumulateUnits(max, textType, matchList, jsonSkeletonSize, units);
		}

		return matchList;
	}

	private static void _accumulateUnits(
		int max, String textType, List<String> matchList, int jsonSkeletonSize,
		String[] units) {
		StringBuffer unitsAccumulatorStorage = new StringBuffer();

		byte[] spaceSize = StringPool.SPACE.getBytes();

		int spaceLength = spaceSize.length;

		for (String unit : units) {
			if (Validator.isNotNull(unit)) {
				byte[] unitSize = unit.getBytes();

				String unitsAccumulator = unitsAccumulatorStorage.toString();
				String cleanUnitsAccumulator = unitsAccumulator.trim();

				byte[] unitsAccumulatorSize = cleanUnitsAccumulator.getBytes();

				if (Validator.isNotNull(cleanUnitsAccumulator) &&
					unitsAccumulatorSize.length + jsonSkeletonSize +
					unitSize.length + spaceLength > max) {

					matchList.add(
						_getAnnotateDocumentPayload(
							cleanUnitsAccumulator,
							textType));
					unitsAccumulatorStorage = new StringBuffer();
				}

				unitsAccumulatorStorage.append(unit + StringPool.SPACE);
			}
		}
		String unitsAccumulator = unitsAccumulatorStorage.toString();
		String cleanUnitsAccumulator = unitsAccumulator.trim();

		byte[] unitsAccumulatorSize = cleanUnitsAccumulator.getBytes();
		if (Validator.isNotNull(cleanUnitsAccumulator)) {
			if (
				unitsAccumulatorSize.length + jsonSkeletonSize
				> max && cleanUnitsAccumulator.contains(" ")) {
				String[] newUnits = cleanUnitsAccumulator.split(" ", max);
				_accumulateUnits(
					max, textType, matchList, jsonSkeletonSize, newUnits);
			}
			else {
				matchList.add(
					_getAnnotateDocumentPayload(
						cleanUnitsAccumulator, textType));
			}
		}
	}

	private static String _getAnnotateDocumentPayload(
		String content, String textType) {

		JSONObject jsonObject = JSONUtil.put(
			"document",
			JSONUtil.put(
				"type", textType
			).put(
				"content", content
			));

		return jsonObject.toString();
	}

}