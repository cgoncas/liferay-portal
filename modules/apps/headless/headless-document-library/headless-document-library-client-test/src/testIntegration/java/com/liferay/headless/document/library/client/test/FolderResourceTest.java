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

package com.liferay.headless.document.library.client.test;

import com.liferay.portal.kernel.util.StringUtil;

import io.restassured.RestAssured;

import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunAsClient
@RunWith(Arquillian.class)
public class FolderResourceTest {

	@Before
	public void setUp() throws MalformedURLException {
		String groupName = StringUtil.randomString(10);

		_groupId = Long.valueOf(
			RestAssured.given(
			).auth(
			).preemptive(
			).basic(
				"test@liferay.com", "test"
			).header(
				"Accept", "application/json"
			).when(
			).param(
				"virtualHost", "localhost"
			).param(
				"parentGroupId", 0
			).param(
				"liveGroupId", 0
			).param(
				"name", groupName
			).param(
				"description", ""
			).param(
				"type", 1
			).param(
				"manualMembership", true
			).param(
				"membershipRestriction", 0
			).param(
				"friendlyURL", "/" + groupName
			).param(
				"site", true
			).param(
				"active", true
			).get(
				new URL(_url, "/api/jsonws/group/add-group")
			).then(
			).statusCode(
				200
			).extract(
			).path(
				"groupId"
			)
		);
	}

	@After
	public void tearDown() throws MalformedURLException {
		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).when(
		).param(
			"groupId", _groupId
		).param(
			"active", true
		).get(
			new URL(_url, "/api/jsonws/group/delete-group")
		).then(
		).statusCode(
			200
		);
	}

	@Test
	public void testDeleteFolder() throws MalformedURLException {
		int folderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testDeleteFolder description\"," +
				"\"name\":\"testDeleteFolder\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).extract(
		).path(
			"id"
		);

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).when(
		).delete(
			new URL(
				_url, "/o/headless-document-library/1.0.0/folder/" + folderId)
		).then(
		).statusCode(
			200
		);
	}

	@Test
	public void testGetDocumentsRepositoryFolderPage()
		throws MalformedURLException {

		int folderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testGetDocumentsRepositoryFolderPage " +
				"description\",\"name\":\"" +
					"testGetDocumentsRepositoryFolderPage\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).statusCode(
			200
		).extract(
		).path(
			"id"
		);

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).when(
		).get(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder/")
		).then(
		).statusCode(
			200
		).body(
			"itemsPerPage", Matchers.equalTo(1)
		).body(
			"lastPageNumber", Matchers.equalTo(1)
		).body(
			"pageNumber", Matchers.equalTo(1)
		).body(
			"totalCount", Matchers.equalTo(1)
		).body(
			"items[0].dateCreated", IsNull.nullValue()
		).body(
			"items[0].dateModified", IsNull.nullValue()
		).body(
			"items[0].description",
			Matchers.equalTo("testGetDocumentsRepositoryFolderPage description")
		).body(
			"items[0].documents", IsNull.nullValue()
		).body(
			"items[0].folders", IsNull.nullValue()
		).body(
			"items[0].id", Matchers.equalTo(folderId)
		).body(
			"items[0].name",
			Matchers.equalTo("testGetDocumentsRepositoryFolderPage")
		).body(
			"items[0].self", IsNull.nullValue()
		).body(
			"items[0].subFolders", IsNull.nullValue()
		);
	}

	@Test
	public void testGetFolder() throws MalformedURLException {
		int folderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testGetFolder description\",\"name\":\"" +
				"testGetFolder\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).extract(
		).path(
			"id"
		);

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).when(
		).get(
			new URL(
				_url, "/o/headless-document-library/1.0.0/folder/" + folderId)
		).then(
		).statusCode(
			200
		).body(
			"dateCreated", IsNull.nullValue()
		).body(
			"dateModified", IsNull.nullValue()
		).body(
			"description", Matchers.equalTo("testGetFolder description")
		).body(
			"documents", IsNull.nullValue()
		).body(
			"folders", IsNull.nullValue()
		).body(
			"id", IsNull.notNullValue()
		).body(
			"name", Matchers.equalTo("testGetFolder")
		).body(
			"self", IsNull.nullValue()
		).body(
			"subFolders", IsNull.nullValue()
		);
	}

	@Test
	public void testGetFolderFolderPage() throws MalformedURLException {
		int parentFolderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"" + StringUtil.randomString(10) + "\"," +
				"\"name\":\"" + StringUtil.randomString(10) + "\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).extract(
		).path(
			"id"
		);

		int folderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testGetFolderFolderPage description\"," +
				"\"name\":\"testGetFolderFolderPage\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/folder/" + parentFolderId +
					"/folder")
		).then(
		).extract(
		).path(
			"id"
		);

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).when(
		).get(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/folder/" + parentFolderId +
					"/folder/")
		).then(
		).statusCode(
			200
		).body(
			"itemsPerPage", Matchers.equalTo(1)
		).body(
			"lastPageNumber", Matchers.equalTo(1)
		).body(
			"pageNumber", Matchers.equalTo(1)
		).body(
			"totalCount", Matchers.equalTo(1)
		).body(
			"items[0].dateCreated", IsNull.nullValue()
		).body(
			"items[0].dateModified", IsNull.nullValue()
		).body(
			"items[0].description",
			Matchers.equalTo("testGetFolderFolderPage description")
		).body(
			"items[0].documents", IsNull.nullValue()
		).body(
			"items[0].folders", IsNull.nullValue()
		).body(
			"items[0].id", Matchers.equalTo(folderId)
		).body(
			"items[0].name", Matchers.equalTo("testGetFolderFolderPage")
		).body(
			"items[0].self", IsNull.nullValue()
		).body(
			"items[0].subFolders", IsNull.nullValue()
		);
	}

	@Test
	public void testPostDocumentsRepositoryFolder()
		throws MalformedURLException {

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testPostDocumentsRepositoryFolder " +
				"description\",\"name\":\"testPostDocumentsRepositoryFolder\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).statusCode(
			200
		).body(
			"dateCreated", IsNull.nullValue()
		).body(
			"dateModified", IsNull.nullValue()
		).body(
			"description",
			Matchers.equalTo("testPostDocumentsRepositoryFolder description")
		).body(
			"documents", IsNull.nullValue()
		).body(
			"folders", IsNull.nullValue()
		).body(
			"id", IsNull.notNullValue()
		).body(
			"name", Matchers.equalTo("testPostDocumentsRepositoryFolder")
		).body(
			"self", IsNull.nullValue()
		).body(
			"subFolders", IsNull.nullValue()
		);
	}

	@Test
	public void testPostFolderFolder() throws MalformedURLException {
		int parentFolderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"" + StringUtil.randomString(10) + "\"," +
				"\"name\":\"" + StringUtil.randomString(10) + "\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).statusCode(
			200
		).extract(
		).path(
			"id"
		);

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testPostFolderFolder description\"," +
				"\"name\":\"testPostFolderFolder\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/folder/" + parentFolderId +
					"/folder")
		).then(
		).statusCode(
			200
		).body(
			"dateCreated", IsNull.nullValue()
		).body(
			"dateModified", IsNull.nullValue()
		).body(
			"description", Matchers.equalTo("testPostFolderFolder description")
		).body(
			"documents", IsNull.nullValue()
		).body(
			"folders", IsNull.nullValue()
		).body(
			"id", IsNull.notNullValue()
		).body(
			"name", Matchers.equalTo("testPostFolderFolder")
		).body(
			"self", IsNull.nullValue()
		).body(
			"subFolders", IsNull.nullValue()
		);
	}

	@Test
	public void testUpdateFolder() throws MalformedURLException {
		int folderId = RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testUpdateFolder description\"," +
				"\"name\":\"testUpdateFolder\"}"
		).when(
		).post(
			new URL(
				_url,
				"/o/headless-document-library/1.0.0/documents-repository/" +
					_groupId + "/folder")
		).then(
		).statusCode(
			200
		).extract(
		).path(
			"id"
		);

		RestAssured.given(
		).auth(
		).preemptive(
		).basic(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"testUpdateFolder description updated\"," +
				"\"name\":\"testUpdateFolder updated\"}"
		).when(
		).put(
			new URL(
				_url, "/o/headless-document-library/1.0.0/folder/" + folderId)
		).then(
		).statusCode(
			200
		).body(
			"dateCreated", IsNull.nullValue()
		).body(
			"dateModified", IsNull.nullValue()
		).body(
			"description",
			Matchers.equalTo("testUpdateFolder description updated")
		).body(
			"documents", IsNull.nullValue()
		).body(
			"folders", IsNull.nullValue()
		).body(
			"id", IsNull.notNullValue()
		).body(
			"name", Matchers.equalTo("testUpdateFolder updated")
		).body(
			"self", IsNull.nullValue()
		).body(
			"subFolders", IsNull.nullValue()
		);
	}

	private long _groupId;

	@ArquillianResource
	private URL _url;

}