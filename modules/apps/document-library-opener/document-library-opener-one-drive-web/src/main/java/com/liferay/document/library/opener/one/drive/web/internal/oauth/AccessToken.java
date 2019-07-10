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

import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * @author Cristina González
 */
public class AccessToken {

	public AccessToken(OAuth2AccessToken oAuth2AccessToken) {
		if (oAuth2AccessToken == null) {
			throw new IllegalArgumentException("Access Token is null");
		}

		_oAuth2AccessToken = oAuth2AccessToken;
	}

	public String getAccessToken() {
		return _oAuth2AccessToken.getAccessToken();
	}

	public String getRefreshToken() {
		return _oAuth2AccessToken.getRefreshToken();
	}

	public boolean isValid() {
		if (_oAuth2AccessToken.getExpiresIn() > 0) {
			return true;
		}

		return false;
	}

	private final OAuth2AccessToken _oAuth2AccessToken;

}