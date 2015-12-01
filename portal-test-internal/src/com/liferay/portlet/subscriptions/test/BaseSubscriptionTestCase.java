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

package com.liferay.portlet.subscriptions.test;

import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import org.junit.Before;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
public abstract class BaseSubscriptionTestCase {

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		creatorUser = UserTestUtil.addGroupUser(
			group, RoleConstants.SITE_MEMBER);
	}

	public void tearDown() throws Exception {
		GroupLocalServiceUtil.deleteGroup(group);

		UserLocalServiceUtil.deleteUser(user);

		UserLocalServiceUtil.deleteUser(creatorUser);
	}

	protected long addBaseModel(long userId, long containerModelId)
		throws Exception {

		return 0;
	}

	protected long addContainerModel(long userId, long containerModelId)
		throws Exception {

		return 0;
	}

	protected void updateBaseModel(long userId, long baseModelId)
		throws Exception {
	}

	protected static final long PARENT_CONTAINER_MODEL_ID_DEFAULT = 0;

	protected User creatorUser;
	protected Group group;
	protected User user;

}