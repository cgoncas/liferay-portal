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
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.portal.vulcan.context.AcceptLanguage;
import com.liferay.portal.vulcan.dto.Page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Julio Camarero
 */
@RunWith(Arquillian.class)
public class ResourcePermissionStructuredContentImplTest {

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
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndGuestUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndNoSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndGroupPermissionAndSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndAdminUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndGuestUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndNoSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithGuestPermissionAndNoGroupPermissionAndSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndAdminUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndGuestUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(0, structuredContentPage.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndNoSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(0, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndGroupPermissionAndSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(true);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndAdminUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(1, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndGuestUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(0, structuredContentPage.getTotalCount());
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndNoSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(0, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetPageItemsWithNoGuestPermissionAndNoGroupPermissionAndSiteUser()
		throws Throwable {

		Map<Locale, String> stringMap = new HashMap<>();

		stringMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT,
			RandomTestUtil.randomString(), false, stringMap, stringMap,
			stringMap, null, LocaleUtil.US, null, true, true, serviceContext);

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Page<StructuredContent> structuredContentPage =
				_structuredContentResource.
					getContentSpaceStructuredContentsPage(
						_group.getGroupId(), "", "", _acceptLanguage,
						PaginationRequest.of(10, 1));

			Assert.assertEquals(0, structuredContentPage.getTotalCount());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private static final AcceptLanguage _acceptLanguage =
		AcceptLanguageRequest.of(
			Collections.singletonList(LocaleUtil.US), LocaleUtil.US);

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private StructuredContentResource _structuredContentResource;

	@Inject
	private UserLocalService _userLocalService;

}