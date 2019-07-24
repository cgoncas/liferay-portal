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

package com.liferay.document.library.repository.portlet.repository.impl;

import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.repository.liferayrepository.LiferayRepositoryFactory;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Cristina González
 */
@Component(
	property = "repository.target.class.name=com.liferay.portal.repository.portletrepository.PortletRepository",
	service = RepositoryFactory.class
)
public class PortletRepositoryFactory implements RepositoryFactory {

	@Activate
	public void activate() {
		_liferayRepositoryFactory = new LiferayRepositoryFactory();
	}

	@Override
	public LocalRepository createLocalRepository(long repositoryId) {
		return _liferayRepositoryFactory.createLocalRepository(repositoryId);
	}

	@Override
	public Repository createRepository(long repositoryId) {
		return _liferayRepositoryFactory.createRepository(repositoryId);
	}

	private LiferayRepositoryFactory _liferayRepositoryFactory;

}