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

import static com.liferay.portal.users.rest.provider.UserBodyWriter.toRestUser;

import com.liferay.portal.model.User;
import com.liferay.portal.users.rest.api.model.RestUser;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Collection;

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
public class CollectionUserBodyWriter
	implements MessageBodyWriter<Collection<User>> {

	@Context
	private Providers _providers;

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		if (Collection.class.isAssignableFrom(type)) {
			if (genericType instanceof ParameterizedType) {
				ParameterizedType parameterizedType =
					(ParameterizedType)genericType;

				Type classType = parameterizedType.getActualTypeArguments()[0];

				if (classType instanceof Class) {
					return User.class.isAssignableFrom((Class)classType);
				}
			}
		}

		return false;
	}

	@Override
	public long getSize(
		Collection<User> users, Class<?> type, Type genericType,
		Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	@Override
	public void writeTo(
			Collection<User> users, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		Collection<RestUser> restUsers = new ArrayList<>(users.size());

		for (User user : users) {
			restUsers.add(toRestUser(user, _request));
		}

		@SuppressWarnings("rawtypes")
		MessageBodyWriter<Collection> messageBodyWriter =
			_providers.getMessageBodyWriter(
				Collection.class, RestUser.class, annotations, mediaType);

		messageBodyWriter.writeTo(
			restUsers, Collection.class, RestUser.class, annotations, mediaType,
			httpHeaders, entityStream);
	}

	@Context HttpServletRequest _request;
}