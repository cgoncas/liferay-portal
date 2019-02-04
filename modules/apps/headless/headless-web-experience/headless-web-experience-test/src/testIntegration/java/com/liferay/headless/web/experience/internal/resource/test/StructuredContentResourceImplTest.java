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

package com.liferay.headless.web.experience.internal.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.web.experience.dto.StructuredContent;
import com.liferay.headless.web.experience.internal.resource.test.util.AcceptLanguageRequest;
import com.liferay.headless.web.experience.internal.resource.test.util.PaginationRequest;
import com.liferay.headless.web.experience.resource.StructuredContentResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.portal.vulcan.dto.Page;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class StructuredContentResourceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetContentSpaceStructuredContentsPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Page<StructuredContent> structuredContentCollection =
			_structuredContentResource.getContentSpaceStructuredContentsPage(
				_group.getGroupId(), "", "",
				AcceptLanguageRequest.of(
					Collections.singletonList(LocaleUtil.US), LocaleUtil.US),
				PaginationRequest.of(10, 1));

		List<StructuredContent> structuredContents =
			(List<StructuredContent>)structuredContentCollection.getItems();

		Assert.assertFalse(structuredContents.isEmpty());

		StructuredContent structuredContent = structuredContents.get(0);

		Assert.assertEquals(
			Long.valueOf(journalArticle.getResourcePrimKey()),
			structuredContent.getId());
	}

	@Test
	public void testGetContentSpaceStructuredContentsPageNonexistingGroup()
		throws Exception {

		try {
			_structuredContentResource.getContentSpaceStructuredContentsPage(
				99999999999L, "", "",
				AcceptLanguageRequest.of(
					Collections.singletonList(LocaleUtil.US), LocaleUtil.US),
				PaginationRequest.of(10, 1));
			Assert.fail("Should throw NotFoundException");
		}
		catch (NotFoundException nfe) {
			Assert.assertEquals("HTTP 404 Not Found", nfe.getMessage());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private StructuredContentResource _structuredContentResource;

}