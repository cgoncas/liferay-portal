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
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.media.object.apio.architect.model.MediaObject;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.io.ByteArrayInputStream;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ruben Pulido
 */
public class BaseMediaObjectNestedCollectionResourceTest {

	protected FileEntry addFileEntry(
			long groupId, ServiceContext serviceContext)
		throws Exception {

		return DLAppServiceUtil.addFileEntry(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null, null,
			RandomTestUtil.randomString(10), RandomTestUtil.randomString(10),
			StringPool.BLANK, new ByteArrayInputStream(_CONTENT.getBytes()), 0L,
			serviceContext);
	}

	protected FileEntry addMediaObject(long groupId, MediaObject mediaObject)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			_getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_addFileEntry", long.class, MediaObject.class);

		method.setAccessible(true);

		return (FileEntry)method.invoke(
			_getNestedCollectionResource(), groupId, mediaObject);
	}

	protected String getFileEntryPreviewURL(FileEntry fileEntry)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			_getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getFileEntryPreviewURL", FileEntry.class);

		method.setAccessible(true);

		return (String)method.invoke(_getNestedCollectionResource(), fileEntry);
	}

	protected List<String> getMediaObjectAssetTags(FileEntry fileEntry)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			_getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getMediaObjectAssetTags", FileEntry.class);

		method.setAccessible(true);

		return (List<String>)method.invoke(
			_getNestedCollectionResource(), fileEntry);
	}

	protected PageItems<FileEntry> getPageItems(
			Pagination pagination, long groupId)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			_getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getPageItems", Pagination.class, long.class);

		method.setAccessible(true);

		return (PageItems)method.invoke(
			_getNestedCollectionResource(), pagination, groupId);
	}

	private NestedCollectionResource _getNestedCollectionResource()
		throws Exception {

		Registry registry = RegistryUtil.getRegistry();

		Collection<NestedCollectionResource> collection = registry.getServices(
			NestedCollectionResource.class,
			"(component.name=com.liferay.media.object.apio.internal." +
				"architect.resource.MediaObjectNestedCollectionResource)");

		Iterator<NestedCollectionResource> iterator = collection.iterator();

		return iterator.next();
	}

	private static final String _CONTENT =
		"Content: Enterprise. Open Source. For Life.";

}