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

package com.liferay.headless.document.library.internal.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.document.library.dto.Folder;
import com.liferay.headless.document.library.resource.FolderResource;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.portal.vulcan.context.Pagination;
import com.liferay.portal.vulcan.dto.Page;

import java.util.Collection;
import java.util.Iterator;

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
public class FolderResourceImplTest {

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
	public void testGetDocumentsRepositoryFolderPage() throws Exception {
		_dlAppService.addFolder(
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"My folder name", "My folder description", new ServiceContext());

		Page<Folder> page = _folderResource.getDocumentsRepositoryFolderPage(
			_group.getGroupId(), _createPagination());

		Collection<Folder> folders = page.getItems();

		Assert.assertFalse(folders.isEmpty());

		Iterator<Folder> iterator = folders.iterator();

		Folder folder = iterator.next();

		Assert.assertEquals("My folder description", folder.getDescription());
		Assert.assertEquals("My folder name", folder.getName());
	}

	private Pagination _createPagination() {
		return new Pagination() {

			@Override
			public int getEndPosition() {
				return 1;
			}

			@Override
			public int getItemsPerPage() {
				return 10;
			}

			@Override
			public int getPageNumber() {
				return 1;
			}

			@Override
			public int getStartPosition() {
				return 0;
			}

		};
	}

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private FolderResource _folderResource;

	@DeleteAfterTestRun
	private Group _group;

}