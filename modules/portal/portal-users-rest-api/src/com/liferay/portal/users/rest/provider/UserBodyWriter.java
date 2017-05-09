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

import com.liferay.portal.model.User;
import com.liferay.portal.users.rest.api.model.ResourceLink;
import com.liferay.portal.users.rest.api.model.RestUser;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author Carlos Sierra Andrés
 */
@Provider
public class UserBodyWriter implements MessageBodyWriter<User> {

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return User.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(
		User user, Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return -1;
	}

	@Override
	public void writeTo(
			User user, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		RestUser restUser = toRestUser(user, _request);

		MessageBodyWriter<RestUser> restUserMessageBodyWriter =
			_providers.getMessageBodyWriter(
				RestUser.class, RestUser.class, annotations, mediaType);

		restUserMessageBodyWriter.writeTo(
			restUser, RestUser.class, RestUser.class, annotations, mediaType,
			httpHeaders, entityStream);
	}

	public static RestUser toRestUser(User user, HttpServletRequest request)
		throws MalformedURLException {

		RestUser restUser = new RestUser(user);

		restUser.setPortraitLink(new ResourceLink());

		restUser.setPortraitLink(
			new ResourceLink(
				new URL(
					request.getScheme() + "://" + request.getServerName() +
						":" + request.getServerPort() +
						request.getContextPath() + "/api/users/" +
						user.getUserId() + "/portrait")));

		return restUser;
	}

	@Context
	private Providers _providers;

	@Context
	HttpServletRequest _request;
}