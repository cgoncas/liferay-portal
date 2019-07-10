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

package com.liferay.document.library.opener.one.drive.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.opener.one.drive.web.internal.oauth.AccessToken;
import com.liferay.document.library.opener.one.drive.web.internal.oauth.OAuth2Manager;
import com.liferay.document.library.opener.one.drive.web.internal.oauth.OAuth2StateUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;

import java.util.Optional;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"auth.token.ignore.mvc.action=true",
		"javax.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"javax.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/edit_in_office365"
	},
	service = MVCActionCommand.class
)
public class EditInOneDriveMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long companyId = _portal.getCompanyId(actionRequest);
		long userId = _portal.getUserId(actionRequest);

		Optional<AccessToken> accessTokenOptional =
			_oAuth2Manager.getAccessTokenOptional(companyId, userId);

		if (accessTokenOptional.isPresent()) {
			_executeCommand(actionRequest, accessTokenOptional.get());
		}
		else {
			String state = PwdGenerator.getPassword(5);

			OAuth2StateUtil.save(
				_portal.getHttpServletRequest(actionRequest), userId,
				_getSuccessURL(actionRequest), _getFailureURL(actionRequest),
				state);

			actionResponse.sendRedirect(
				_oAuth2Manager.getAuthorizationURL(
					companyId, _portal.getPortalURL(actionRequest), state));
		}
	}

	private void _executeCommand(
			ActionRequest actionRequest, AccessToken accessToken)
		throws PortalException {

		System.out.println(
			"Logged successfully " + accessToken.getAccessToken());
	}

	private String _getFailureURL(PortletRequest portletRequest)
		throws PortalException {

		LiferayPortletURL liferayPortletURL = _portletURLFactory.create(
			portletRequest, _portal.getPortletId(portletRequest),
			_portal.getControlPanelPlid(portletRequest),
			PortletRequest.RENDER_PHASE);

		return liferayPortletURL.toString();
	}

	private String _getSuccessURL(PortletRequest portletRequest) {
		return _portal.getCurrentURL(
			_portal.getHttpServletRequest(portletRequest));
	}

	@Reference
	private OAuth2Manager _oAuth2Manager;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}