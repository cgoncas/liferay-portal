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
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

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
	}

}