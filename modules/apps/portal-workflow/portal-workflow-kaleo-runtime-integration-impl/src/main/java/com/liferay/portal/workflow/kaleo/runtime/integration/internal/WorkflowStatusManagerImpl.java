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

package com.liferay.portal.workflow.kaleo.runtime.integration.internal;

import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowStatusManager;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bruno Farache
 */
@Component(service = WorkflowStatusManager.class)
public class WorkflowStatusManagerImpl implements WorkflowStatusManager {

	@Override
	public void updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws WorkflowException {

		try {
			WorkflowHandlerRegistryUtil.updateStatus(status, workflowContext);
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

}