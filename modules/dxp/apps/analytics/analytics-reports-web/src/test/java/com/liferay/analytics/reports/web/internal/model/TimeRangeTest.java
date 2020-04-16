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

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.time.LocalDateTime;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author David Arques
 */
public class TimeRangeTest {

	@Test
	public void testGetIntervalLocalDateTimesWithLast7DaysTimeSpan() {
		TimeRange timeRange = TimeRange.of(TimeSpan.LAST_7_DAYS, 0);

		List<LocalDateTime> intervalLocalDateTimes =
			timeRange.getIntervalLocalDateTimes();

		Assert.assertEquals(
			intervalLocalDateTimes.toString(), 7,
			intervalLocalDateTimes.size());
		Assert.assertEquals(
			timeRange.getStartLocalDateTime(), intervalLocalDateTimes.get(0));
	}

	@Test
	public void testGetIntervalLocalDateTimesWithLast30DaysTimeSpan() {
		TimeRange timeRange = TimeRange.of(TimeSpan.LAST_30_DAYS, 0);

		List<LocalDateTime> intervalLocalDateTimes =
			timeRange.getIntervalLocalDateTimes();

		Assert.assertEquals(
			intervalLocalDateTimes.toString(), 30,
			intervalLocalDateTimes.size());
		Assert.assertEquals(
			timeRange.getStartLocalDateTime(), intervalLocalDateTimes.get(0));
	}

	@Test
	public void testOf() {
		TimeSpan timeSpan = TimeSpan.LAST_7_DAYS;
		int timeSpanOffset = RandomTestUtil.randomInt();

		TimeRange timeRange = TimeRange.of(timeSpan, timeSpanOffset);

		Assert.assertNotNull(timeRange);
		Assert.assertEquals(timeSpan, timeRange.getTimeSpan());
		Assert.assertEquals(timeSpanOffset, timeRange.getTimeSpanOffset());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOfWithNegativeTimeSpanOffset() {
		TimeRange.of(TimeSpan.LAST_7_DAYS, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOfWithNullKey() {
		TimeRange.of(null, RandomTestUtil.randomInt());
	}

}