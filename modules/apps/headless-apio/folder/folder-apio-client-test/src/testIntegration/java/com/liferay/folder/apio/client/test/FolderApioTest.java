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

package com.liferay.folder.apio.client.test;

import com.liferay.folder.apio.client.test.internal.activator.FolderTestActivator;
import com.liferay.oauth2.provider.test.util.OAuth2ProviderTestUtil;
import com.liferay.portal.apio.test.util.ApioClientBuilder;

import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ruben Pulido
 */
@RunAsClient
@RunWith(Arquillian.class)
public class FolderApioTest {

	@Deployment
	public static Archive<?> getArchive() throws Exception {
		return OAuth2ProviderTestUtil.getArchive(FolderTestActivator.class);
	}

	@Before
	public void setUp() throws MalformedURLException {
		_rootEndpointURL = new URL(_url, "/o/api");
	}

	@Test
	public void testCreateFolder() {
		String path = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME +
					"'}._links.documentsRepository.href"
		).then(
		).extract(
		).path(
			"_links.folders.href"
		);

		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"My description\",\"name\":\"My folder\"}"
		).when(
		).post(
			path
		).then(
		).statusCode(
			200
		).body(
			"dateCreated", IsNull.notNullValue()
		).body(
			"dateModified", IsNull.notNullValue()
		).body(
			"description", Matchers.equalTo("My description")
		).body(
			"name", Matchers.equalTo("My folder")
		).body(
			"_links.self.href", IsNull.notNullValue()
		);
	}

	@Test
	public void testCreateSubfolder() {
		String subfoldersPath = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME +
					"'}._links.documentsRepository.href"
		).follow(
			"_links.folders.href"
		).then(
		).statusCode(
			200
		).extract(
		).path(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}._links.subFolders.href"
		);

		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"My subfolder description\",\"name\":\"My " +
				"subfolder\"}"
		).when(
		).post(
			subfoldersPath
		).then(
		).statusCode(
			200
		).body(
			"dateCreated", IsNull.notNullValue()
		).body(
			"dateModified", IsNull.notNullValue()
		).body(
			"description", Matchers.equalTo("My subfolder description")
		).body(
			"name", Matchers.equalTo("My subfolder")
		).body(
			"_links.self.href", IsNull.notNullValue()
		);
	}

	@Test
	public void testDeleteFolder() {
		String foldersHref = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME +
					"'}._links.documentsRepository.href"
		).then(
		).extract(
		).path(
			"_links.folders.href"
		);

		String folderHref = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"Example description\",\"name\":\"Example " +
				"folder\"}"
		).when(
		).post(
			foldersHref
		).then(
		).extract(
		).path(
			"_links.self.href"
		);

		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Content-Type", "application/json"
		).when(
		).delete(
			folderHref
		).then(
		).statusCode(
			200
		);
	}

	@Test
	public void testDocumentsRepositoryContainsLinksToFoldersAndDocuments() {
		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME + "'}._links." +
					"documentsRepository.href"
		).then(
		).statusCode(
			200
		).body(
			"_links.documents.href", IsNull.notNullValue()
		).body(
			"_links.folders.href", IsNull.notNullValue()
		).body(
			"_links.self.href", IsNull.notNullValue()
		);
	}

	@Test
	public void testDocumentsRepositoryLinkExistsInContentSpace() {
		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).then(
		).statusCode(
			200
		).body(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME + "'}._links." +
					"documentsRepository.href",
			IsNull.notNullValue()
		);
	}

	@Test
	public void testGetFolders() {
		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME +
					"'}._links.documentsRepository.href"
		).follow(
			"_links.folders.href"
		).then(
		).statusCode(
			200
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}.dateCreated",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}.dateModified",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}.description",
			Matchers.equalTo(FolderTestActivator.FOLDER_DESCRIPTION)
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}._links.documents",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}._links.self.href",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME +
					"'}._links.subFolders",
			IsNull.notNullValue()
		);
	}

	@Test
	public void testGetSubfolders() {
		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME +
					"'}._links.documentsRepository.href"
		).follow(
			"_links.folders.href"
		).follow(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.FOLDER_NAME + "'}._links.subFolders.href"
		).then(
		).statusCode(
			200
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.SUBFOLDER_NAME + "'}.dateCreated",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.SUBFOLDER_NAME + "'}.dateModified",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.SUBFOLDER_NAME + "'}.description",
			Matchers.equalTo(FolderTestActivator.SUBFOLDER_DESCRIPTION)
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.SUBFOLDER_NAME + "'}._links.documents",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.SUBFOLDER_NAME + "'}._links.self.href",
			IsNull.notNullValue()
		).body(
			"_embedded.Folder.find {it.name == '" +
				FolderTestActivator.SUBFOLDER_NAME + "'}._links.subFolders",
			IsNull.notNullValue()
		);
	}

	@Test
	public void testUpdateFolder() {
		String foldersHref = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).follow(
			"_embedded.ContentSpace.find {it.name == '" +
				FolderTestActivator.CONTENT_SPACE_NAME +
					"'}._links.documentsRepository.href"
		).then(
		).extract(
		).path(
			"_links.folders.href"
		);

		String folderHref = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"Example description 2\",\"name\":\"Example " +
				"folder 2\"}"
		).when(
		).post(
			foldersHref
		).then(
		).extract(
		).path(
			"_links.self.href"
		);

		ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Content-Type", "application/json"
		).body(
			"{\"description\":\"Example description 2 modified\",\"name\":\"" +
				"Example folder 2 modified\"}"
		).when(
		).put(
			folderHref
		).then(
		).body(
			"description", Matchers.equalTo("Example description 2 modified")
		).body(
			"name", Matchers.equalTo("Example folder 2 modified")
		);
	}

	private URL _rootEndpointURL;

	@ArquillianResource
	private URL _url;

}