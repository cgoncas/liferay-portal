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

package com.liferay.staging.processes.web.internal.portlet.action;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Levente Hudák
 */
@Component(
	property = {
		"javax.portlet.name=" + StagingProcessesPortletKeys.STAGING_PROCESSES,
		"mvc.command.name=/staging_processes/delete_background_tasks"
	},
	service = MVCActionCommand.class
)
public class DeleteBackgroundTasksMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_deleteBackgroundTask(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchBackgroundTaskException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/error/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteBackgroundTask(ActionRequest actionRequest)
		throws PortalException {

		long[] backgroundTaskIds = ParamUtil.getLongValues(
			actionRequest, "deleteBackgroundTaskIds");

		for (long backgroundTaskId : backgroundTaskIds) {
			BackgroundTask backgroundTask =
				_backgroundTaskManager.getBackgroundTask(backgroundTaskId);

			if (!backgroundTask.isInProgress()) {
				_backgroundTaskManager.deleteBackgroundTask(backgroundTaskId);
			}
		}
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

}