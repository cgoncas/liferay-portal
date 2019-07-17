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

package com.liferay.document.library.opener.one.drive.web.internal;

import java.io.File;

import java.util.function.Supplier;

/**
 * Represents a link between a Documents and Media {@code FileEntry} and a
 * Office 365 file. The methods in {@link DLOpenerOneDriveManager} return
 * instances of this class, which you should never create directly.
 *
 * @author Cristina González Castellano
 * @review
 */
public class DLOpenerOneDriveFileReference {

	/**
	 * Creates a new {@code DLOpenerOneDriveFileReference}.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param titleSupplier the supplier that provides the document's title when
	 *        invoked
	 * @param fileSupplier the supplier that provides the document's contents
	 *        when invoked
	 * @review
	 */
	public DLOpenerOneDriveFileReference(
		long fileEntryId, Supplier<String> titleSupplier,
		Supplier<File> fileSupplier, Supplier<String> urlSupplier) {

		_fileEntryId = fileEntryId;
		_titleSupplier = titleSupplier;
		_fileSupplier = fileSupplier;
		_urlSupplier = urlSupplier;
	}

	/**
	 * Returns a file with this One Drive file reference's content.
	 *
	 * @return the file
	 * @review
	 */
	public File getContentFile() {
		return _fileSupplier.get();
	}

	/**
	 * Returns the primary key of the file entry linked to this reference.
	 *
	 * @return the primary key of the file entry
	 * @review
	 */
	public long getFileEntryId() {
		return _fileEntryId;
	}

	/**
	 * Returns this One Drive file reference's title.
	 *
	 * @return the title
	 * @review
	 */
	public String getTitle() {
		return _titleSupplier.get();
	}

	/**
	 * Returns this One Drive file reference's url.
	 *
	 * @return the url
	 * @review
	 */
	public String getUrl() {
		return _urlSupplier.get();
	}

	private final long _fileEntryId;
	private final Supplier<File> _fileSupplier;
	private final Supplier<String> _titleSupplier;
	private final Supplier<String> _urlSupplier;

}