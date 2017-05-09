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

package com.liferay.portal.users.rest.provider;

import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.CompanyLocalService;
import com.liferay.portal.service.VirtualHostLocalService;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true, property = {"liferay.provider=true"},
	service = TokenRequestFilter.class
)
public class TokenRequestFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		String host = requestContext.getHeaders().getFirst("Host");

		VirtualHost virtualHost = _virtualHostLocalService.fetchVirtualHost(
			host);

		if (virtualHost == null) {
			return;
		}

		long companyId = virtualHost.getCompanyId();

		requestContext.setProperty(
			"liferay.company", _companyService.fetchCompanyById(companyId));
	}

	@Reference
	public void setCompanyService(CompanyLocalService companyService) {
		_companyService = companyService;
	}

	@Reference
	public void setVirtualHostService(
		VirtualHostLocalService virtualHostLocalService) {

		_virtualHostLocalService = virtualHostLocalService;
	}

	private CompanyLocalService _companyService;
	private VirtualHostLocalService _virtualHostLocalService;

}