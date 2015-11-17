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

package com.liferay.wiki;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.rule.TransactionalTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageResourceLocalServiceUtil;
import com.liferay.wiki.service.persistence.WikiPageResourceUtil;
import com.liferay.wiki.util.test.WikiTestUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
@Sync
public class WikiSelectTest {

	@Before
	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testWikiSelect() throws Exception {

		CacheRegistryUtil.setActive(false);
		_company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.addCompanyAdminUser(_company);

		Group group = GroupTestUtil.addGroup(_company.getCompanyId(), user.getUserId(), 0);

		System.out.println(
			"Company " + _company.getCompanyId() +
				" Group " + _company.getGroupId());

		WikiNode wikiNode = WikiTestUtil.addNode(
			user.getUserId(), group.getGroupId(), "Node", "Vale");

		WikiPage wikiPage = WikiTestUtil.addPage(
			user.getUserId(), group.getGroupId(), wikiNode.getNodeId(), "Page",
			true);

		WikiPageResource initialWikiPageResource =
			WikiPageResourceLocalServiceUtil.getPageResource(
				wikiPage.getResourcePrimKey());

		Assert.assertEquals(
			0, initialWikiPageResource.getCompanyId());

		DB db = DBFactoryUtil.getDB();

		db.runSQL(getUpdateSQL(
			"WikiPageResource", "nodeId", "WikiNode", "nodeId"));

		WikiPageResource actualWikiPageResource =
			WikiPageResourceLocalServiceUtil.getPageResource(
				wikiPage.getResourcePrimKey());

		Assert.assertEquals(
			_company.getCompanyId(), actualWikiPageResource.getCompanyId());

		CacheRegistryUtil.setActive(true);

	}

	protected String getUpdateSQL(
		String tableName, String columnName,
		String foreignTableName, String foreignColumnName) {

		StringBundler sb = new StringBundler(10);

		sb.append("select companyId from ");
		sb.append(foreignTableName);
		sb.append(" where ");
		sb.append(foreignTableName);
		sb.append(".");
		sb.append(foreignColumnName);
		sb.append(" = ");
		sb.append(tableName);
		sb.append(".");
		sb.append(columnName);

		String selectSQL =  sb.toString();

		sb = new StringBundler(5);

		sb.append("update ");
		sb.append(tableName);
		sb.append(" set companyId = (");
		sb.append(selectSQL);
		sb.append(")");

		System.out.println("Update select" + sb.toString());

		return sb.toString();
	}

	@DeleteAfterTestRun
	private Company _company;
}