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

package com.liferay.headless.document.library.internal.resource;

import com.liferay.headless.document.library.dto.Folder;
import com.liferay.headless.document.library.resource.FolderResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.vulcan.context.AcceptLanguage;
import com.liferay.portal.vulcan.context.Pagination;
import com.liferay.portal.vulcan.dto.Page;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Generated;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseFolderResourceImpl implements FolderResource {

	@Override
	public Response deleteFolder(Long folderId) throws Exception {
		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Folder getDocumentsRepository(Long id) throws Exception {
		return new Folder();
	}

	@Override
	public Page<Folder> getDocumentsRepositoryFolderPage(
			Long documentsRepositoryId, Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	@Override
	public Folder getFolder(Long folderId) throws Exception {
		return new Folder();
	}

	@Override
	public Page<Folder> getFolderFolderPage(
			Long folderId, Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	@Override
	public Folder postDocumentsRepositoryFolder(
		Long documentsRepositoryId, Folder folder)
		throws Exception {

		return new Folder();
	}

	@Override
	public Folder postDocumentsRepositoryFolderBatchCreate(
			Long documentsRepositoryId)
		throws Exception {

		return new Folder();
	}

	@Override
	public Folder postFolderFolder(Long folderId, Folder folder)
		throws Exception {
		return new Folder();
	}

	@Override
	public Folder postFolderFolderBatchCreate(Long folderId) throws Exception {
		return new Folder();
	}

	@Override
	public Folder putFolder(Long folderId, Folder folder) throws Exception {
		return new Folder();
	}

	protected <T, R> List<R> transform(
		List<T> list, Function<T, R> transformFunction) {

		return TransformUtil.transform(list, transformFunction);
	}

	@Context
	protected AcceptLanguage acceptLanguage;

	@Context
	protected Company company;

}