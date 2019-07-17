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

package com.liferay.document.library.opener.one.drive.web.internal.app;

import com.liferay.connected.app.ConnectedApp;
import com.liferay.connected.app.ConnectedAppProvider;
import com.liferay.document.library.opener.one.drive.web.internal.DLOpenerOneDriveManager;
import com.liferay.document.library.opener.one.drive.web.internal.oauth.AccessToken;
import com.liferay.document.library.opener.one.drive.web.internal.oauth.OAuth2Manager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(immediate = true, service = ConnectedAppProvider.class)
public class OneDriveConnectedAppProvider implements ConnectedAppProvider {

	@Override
	public ConnectedApp getConnectedApp(User user) throws PortalException {
		if (!_dlOpenerOneDriveManager.isConfigured(user.getCompanyId())) {
			return null;
		}

		Optional<AccessToken> accessTokenOptional =
			_oAuth2Manager.getAccessTokenOptional(
				user.getCompanyId(), user.getUserId());

		return accessTokenOptional.map(
			accessToken -> new ConnectedApp() {

				@Override
				public String getImageURL() {
					return _servletContext.getContextPath() +
						"/images/one_drive.png";
				}

				@Override
				public String getKey() {
					return "one-drive";
				}

				@Override
				public String getName(Locale locale) {
					ResourceBundle resourceBundle =
						ResourceBundleUtil.getBundle(locale, getClass());

					StringBundler sb = new StringBundler(5);

					sb.append(LanguageUtil.get(resourceBundle, "one-drive"));

					String emailAddress = _getOneDriveUserEmailAddress(
						accessToken);

					if (Validator.isNotNull(emailAddress)) {
						sb.append(StringPool.SPACE);
						sb.append(StringPool.OPEN_PARENTHESIS);
						sb.append(emailAddress);
						sb.append(StringPool.CLOSE_PARENTHESIS);
					}

					return sb.toString();
				}

				@Override
				public void revoke() throws PortalException {
					_oAuth2Manager.revokeOAuth2AccessToken(
						user.getCompanyId(), user.getUserId());
				}

			}
		).orElseGet(
			() -> null
		);
	}

	private String _getOneDriveUserEmailAddress(AccessToken accessToken) {
		com.microsoft.graph.models.extensions.User user =
			_dlOpenerOneDriveManager.getUser(accessToken);

		return user.mail;
	}

	@Reference
	private DLOpenerOneDriveManager _dlOpenerOneDriveManager;

	@Reference
	private OAuth2Manager _oAuth2Manager;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.opener.google.drive.web)"
	)
	private ServletContext _servletContext;

}