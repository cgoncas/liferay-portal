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

package com.liferay.media.object.apio.internal.architect.resource.test;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.apio.test.util.PaginationRequest;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.io.ByteArrayInputStream;

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
public class ResourcePermissionMediaObjectNestedCollectionResourceTest
	extends BaseMediaObjectNestedCollectionResourceTest {

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
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndAdminUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndGuestUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndNoSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndAdminUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndGuestUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndNoSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndAdminUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndGuestUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(0, pageItems.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndNoSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(0, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndAdminUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(1, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndGuestUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(0, pageItems.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndNoSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(0, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndSiteUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		_addFileEntry(_group.getGroupId(), serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			PageItems<FileEntry> pageItems = getPageItems(
				PaginationRequest.of(10, 1), _group.getGroupId());

			Assert.assertEquals(0, pageItems.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private FileEntry _addFileEntry(long groupId, ServiceContext serviceContext)
		throws Exception {

		String content = RandomTestUtil.randomString(10);

		return DLAppServiceUtil.addFileEntry(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null, null,
			RandomTestUtil.randomString(10), RandomTestUtil.randomString(10),
			StringPool.BLANK, new ByteArrayInputStream(content.getBytes()), 0L,
			serviceContext);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private UserLocalService _userLocalService;

}