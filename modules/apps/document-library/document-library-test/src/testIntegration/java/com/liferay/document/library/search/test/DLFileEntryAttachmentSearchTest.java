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

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Istvan Sajtos
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntryAttachmentSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testSearchIncludeAttachment() throws Exception {
		String title = "title";

		_addFileEntry(title);
		_addWikiPageWithAttachment(title);

		Assert.assertEquals(1, _searchCount(title, false));
		Assert.assertEquals(2, _searchCount(title, true));
	}

	private void _addFileEntry(String title) throws PortalException {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_dlAppLocalService.addFileEntry(
			serviceContext.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.TEXT_PLAIN, title,
			StringPool.BLANK, StringPool.BLANK, _CONTENT.getBytes(),
			serviceContext);
	}

	private void _addWikiPageWithAttachment(String fileName) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setCommand(Constants.ADD);

		WikiNode wikiNode = _wikiNodeLocalService.addNode(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(50), serviceContext);

		String wikiPageTitle = RandomTestUtil.randomString();

		_wikiPageLocalService.addPage(
			serviceContext.getUserId(), wikiNode.getNodeId(), wikiPageTitle,
			RandomTestUtil.randomString(), "Summary", false, serviceContext);

		File file = FileUtil.createTempFile(_CONTENT.getBytes());

		_wikiPageLocalService.addPageAttachment(
			serviceContext.getUserId(), wikiNode.getNodeId(), wikiPageTitle,
			fileName, file, MimeTypesUtil.getExtensionContentType("docx"));
	}

	private int _searchCount(String keywords, boolean includeAttachments)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeAttachments(includeAttachments);
		searchContext.setKeywords(keywords);

		FacetedSearcher facetedSearcher =
			_facetedSearcherManager.createFacetedSearcher();

		Hits hits = facetedSearcher.search(searchContext);

		return hits.getLength();
	}

	private static final String _CONTENT =
		"Content: Enterprise. Open Source. For Life.";

	@Inject
	private static DLAppLocalService _dlAppLocalService;

	@Inject
	private static FacetedSearcherManager _facetedSearcherManager;

	@Inject
	private static WikiNodeLocalService _wikiNodeLocalService;

	@Inject
	private static WikiPageLocalService _wikiPageLocalService;

	@DeleteAfterTestRun
	private Group _group;

}