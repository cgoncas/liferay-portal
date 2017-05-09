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

package com.liferay.portal.users.rest.api.application;

import com.liferay.portal.users.rest.api.resource.UsersResource;
import com.liferay.portal.users.rest.provider.CollectionUserBodyWriter;
import com.liferay.portal.users.rest.provider.CompanyContextProvider;
import com.liferay.portal.users.rest.provider.OptionalBodyWriter;
import com.liferay.portal.users.rest.provider.PageMessageBodyWriter;
import com.liferay.portal.users.rest.provider.PaginationProvider;
import com.liferay.portal.users.rest.provider.PrincipalExceptionMapper;
import com.liferay.portal.users.rest.provider.RestPortraitBodyWriter;
import com.liferay.portal.users.rest.provider.StringArrayBodyWriter;
import com.liferay.portal.users.rest.provider.TokenRequestFilter;
import com.liferay.portal.users.rest.provider.UserBodyWriter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@ApplicationPath("/api/users")
@Component(
	immediate = true, property = "jaxrs.application=true",
	service = Application.class
)
public class Users extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return new HashSet<>(Arrays.asList(
			CompanyContextProvider.class, CollectionUserBodyWriter.class,
			RestPortraitBodyWriter.class, OptionalBodyWriter.class,
			PrincipalExceptionMapper.class, UserBodyWriter.class,
			PageMessageBodyWriter.class, PaginationProvider.class,
			StringArrayBodyWriter.class));
	}

	@Override
	public Set<Object> getSingletons() {
		return new HashSet<>(
			Arrays.asList(
				_companyRequestFilter, _companyContextProvider,
				_usersResource));
	}

	@Reference
	public void setCompanyContextProvider(
		CompanyContextProvider companyContextProvider) {

		_companyContextProvider = companyContextProvider;
	}

	@Reference
	public void setCompanyRequestFilter(
		TokenRequestFilter companyRequestFilter) {

		_companyRequestFilter = companyRequestFilter;
	}

	@Reference
	public void setUsersResource(UsersResource usersResource) {
		_usersResource = usersResource;
	}

	private CompanyContextProvider _companyContextProvider;
	private TokenRequestFilter _companyRequestFilter;
	private UsersResource _usersResource;

}