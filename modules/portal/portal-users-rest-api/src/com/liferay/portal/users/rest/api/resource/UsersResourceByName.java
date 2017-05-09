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

package com.liferay.portal.users.rest.api.resource;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserService;
import com.liferay.portal.users.rest.api.model.Optional;
import com.liferay.portal.users.rest.api.model.RestUser;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true, service = UsersResourceByName.class
)
@Path("/byName")
public class UsersResourceByName {

	@DELETE
	@Path("/{name}")
	public Response deleteUserById(
			@Context Company company, @PathParam("name") String name)
		throws PortalException {

		try {
			long id = _userService.getUserIdByScreenName(
				company.getCompanyId(), name);

			_userService.deleteUser(id);

			return Response.status(204).build();
		}
		catch (NoSuchUserException nsue) {
			return Response.status(204).build();
		}
	}

	@GET
	@Path("/{name}")
	public Optional<RestUser> getUser(
			@Context Company company, @PathParam("name") String name)
		throws PortalException {

		try {
			User user = _userService.getUserByScreenName(
				company.getCompanyId(), name);

			return Optional.of(new RestUser(user));
		}
		catch (NoSuchUserException nsue) {
			return Optional.empty();
		}
	}

	@Reference
	public void setUserService(UserService userService) {
		_userService = userService;
	}

	private UserService _userService;

}