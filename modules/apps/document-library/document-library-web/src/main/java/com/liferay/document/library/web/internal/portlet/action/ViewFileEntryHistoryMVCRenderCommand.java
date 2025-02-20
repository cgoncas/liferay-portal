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

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.web.internal.display.context.DLViewEntryHistoryDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"javax.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/view_file_entry_history"
	},
	service = MVCRenderCommand.class
)
public class ViewFileEntryHistoryMVCRenderCommand
	extends BaseFileEntryMVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			DLViewEntryHistoryDisplayContext.class.getName(),
			new DLViewEntryHistoryDisplayContext(
				_dlAppLocalService, renderRequest, renderResponse));

		return super.render(renderRequest, renderResponse);
	}

	@Override
	protected String getPath() {
		return "/document_library/view_file_entry_history.jsp";
	}

	@Override
	protected void setAttributes(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		super.setAttributes(renderRequest, renderResponse);
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

}