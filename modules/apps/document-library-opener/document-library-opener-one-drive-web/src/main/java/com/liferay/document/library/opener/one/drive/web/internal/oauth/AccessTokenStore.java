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

package com.liferay.document.library.opener.one.drive.web.internal.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cristina González
 */
public class AccessTokenStore {

	public void add(long companyId, long userId, AccessToken accessToken) {
		Map<Long, AccessToken> companyAccessTokenMap =
			_accessTokenMap.computeIfAbsent(
				companyId, key -> new ConcurrentHashMap<>());

		companyAccessTokenMap.put(userId, accessToken);
	}

	public void delete(long companyId, long userId) {
		Map<Long, AccessToken> companyAccessTokenMap =
			_accessTokenMap.computeIfAbsent(
				companyId, key -> new ConcurrentHashMap<>());

		companyAccessTokenMap.remove(userId);
	}

	public Optional<AccessToken> getAccessTokenOptional(
		long companyId, long userId) {

		Map<Long, AccessToken> companyAccessTokenMap =
			_accessTokenMap.getOrDefault(companyId, new HashMap<>());

		return Optional.ofNullable(companyAccessTokenMap.get(userId));
	}

	private final Map<Long, Map<Long, AccessToken>> _accessTokenMap =
		new ConcurrentHashMap<>();

}