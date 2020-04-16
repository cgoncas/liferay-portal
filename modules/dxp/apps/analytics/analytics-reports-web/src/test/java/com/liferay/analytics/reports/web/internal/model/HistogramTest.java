/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.analytics.reports.web.internal.model;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class HistogramTest {

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testNewHistogram() {
		Histogram histogram = new Histogram(Collections.emptyList(), 0);

		Assert.assertEquals(
			Collections.emptyList(), histogram.getHistogramMetrics());
		Assert.assertEquals(0.0, 0.0, histogram.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewHistogramWithNulllHistogramMetrics() {
		new Histogram(null, 0);
	}

	@Test
	public void testToJSONObject() {
		HistogramMetric histogramMetric = new HistogramMetric(
			new Date(), RandomTestUtil.randomDouble());

		Histogram histogram = new Histogram(
			Arrays.asList(histogramMetric), RandomTestUtil.randomDouble());

		JSONObject jsonObject = histogram.toJSONObject();

		Assert.assertEquals(
			JSONUtil.put(
				"histogram",
				JSONUtil.put(
					JSONUtil.put(
						"key", _formatDate(histogramMetric.getKey())
					).put(
						"value", histogramMetric.getValue()
					))
			).put(
				"value", histogram.getValue()
			).toJSONString(),
			jsonObject.toJSONString());
	}

	private String _formatDate(Date date) {
		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

		return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

}