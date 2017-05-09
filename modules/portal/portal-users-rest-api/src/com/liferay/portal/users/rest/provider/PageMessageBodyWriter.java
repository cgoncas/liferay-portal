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

import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.users.rest.api.model.Page;

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
public class PageMessageBodyWriter implements MessageBodyWriter<Page<?>> {

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		if (!(Page.class.isAssignableFrom(type))) {
			return false;
		}

		Class<?> targetClass = null;

		if (genericType instanceof ParameterizedType) {
			ParameterizedType parameterizedType =
				(ParameterizedType)genericType;

			targetClass =
				(Class<?>)parameterizedType.getActualTypeArguments()[0];
		}

		if (targetClass == null) {
			return false;
		}

		return (_providers.getMessageBodyWriter(
			Collection.class, genericType, annotations, mediaType) != null);
	}

	@Override
	public long getSize(
		Page<?> page, Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return -1;
	}

	@Override
	public void writeTo(
			Page<?> page, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		Collection<Object> linkHeader = new ArrayList<>();

		int currentPage = page.getCurrentPage();

		String requestURL = _httpServletRequest.getRequestURL().toString();

		if (page.hasNext()) {
			String nextPageUrl = HttpUtil.setParameter(
				requestURL, "page", currentPage + 1);

			linkHeader.add(link(nextPageUrl, "next"));

			String lastPageUrl = HttpUtil.setParameter(
				requestURL, "page", page.getLastPage());

			linkHeader.add(link(lastPageUrl, "last"));
		}

		if (page.hasPrevious()) {
			String prevPageUrl = HttpUtil.setParameter(
				requestURL, "page", currentPage - 1);

			linkHeader.add(link(prevPageUrl, "prev"));

			String firstPageUrl = HttpUtil.setParameter(requestURL, "page", 1);

			linkHeader.add(link(firstPageUrl, "first"));
		}

		httpHeaders.addAll("Link", linkHeader);

		ParameterizedType parameterizedType = (ParameterizedType)genericType;

		Type targetClass = parameterizedType.getActualTypeArguments()[0];

		@SuppressWarnings("rawtypes")
		MessageBodyWriter<Collection> messageBodyWriter =
			_providers.getMessageBodyWriter(
				Collection.class, genericType, annotations, mediaType);

		messageBodyWriter.writeTo(
			page.getItems(), Collection.class, targetClass, annotations,
			mediaType, httpHeaders, entityStream);
	}

	private String link(String url, String rel) {
		return "<link href=\"" + url + "\" rel=\"" + rel + "\" />";
	}

	@Context
	Providers _providers;

	@Context
	HttpServletRequest _httpServletRequest;
}