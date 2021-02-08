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

package com.liferay.segments.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
@Sync
public class PerformanceSegmentsEntryProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testPerformance() throws Exception {
		Map<Integer, Map<Integer, Long>> results = new HashMap<>();

		for (int i = 0; i < 3; i++) {
			Map<Integer, Long> resultRow = new HashMap<>();

			resultRow.put(10, _testPerformance(10));
			resultRow.put(50, _testPerformance(50));
			resultRow.put(100, _testPerformance(100));
			resultRow.put(150, _testPerformance(150));

			results.put(i, resultRow);
		}

		try {
			File file = new File(
				PropsUtil.get(PropsKeys.LIFERAY_HOME) +
					"/segments-filtering-data.csv");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fileWriter = new FileWriter(file, true);

			for (Map.Entry<Integer, Map<Integer, Long>> entry :
					results.entrySet()) {

				Map<Integer, Long> value = entry.getValue();

				fileWriter.write("i,size,ms\n");

				for (Map.Entry<Integer, Long> valueEntry : value.entrySet()) {
					fileWriter.write(
						entry.getKey() + "," + valueEntry.getKey() + "," +
							valueEntry.getValue() + "\n");
				}
			}

			fileWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private long _testPerformance(int size) throws Exception {
		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		List<SegmentsEntry> segmentsEntries = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			segmentsEntries.add(
				SegmentsTestUtil.addSegmentsEntry(
					_group.getGroupId(), User.class.getName(),
					_user1.getUserId()));

			segmentsEntries.add(
				SegmentsTestUtil.addSegmentsEntry(
					_group.getGroupId(), User.class.getName(),
					_user2.getUserId()));
		}

		Criteria criteria = new Criteria();

		StringBundler sb = new StringBundler();

		for (int i = 0; i < size; i++) {
			sb.append("(segmentsEntryIds eq '%s')");
			sb.append(" and ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		_segmentsEntrySegmentsCriteriaContributor.contribute(
			criteria,
			String.format(
				sb.toString(),
				segmentsEntries.stream(
				).map(
					SegmentsEntry::getSegmentsEntryId
				).toArray()),
			Criteria.Conjunction.AND);

		StopWatch stopWatch = new StopWatch();

		stopWatch.reset();
		stopWatch.start();

		_segmentsEntryProviderRegistry.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId());

		stopWatch.stop();

		System.out.println(size + ":" + stopWatch.getTime());

		return stopWatch.getTime();
	}

	@Inject(
		filter = "segments.criteria.contributor.key=context",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _contextSegmentsCriteriaContributor;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=segments",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor
		_segmentsEntrySegmentsCriteriaContributor;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}