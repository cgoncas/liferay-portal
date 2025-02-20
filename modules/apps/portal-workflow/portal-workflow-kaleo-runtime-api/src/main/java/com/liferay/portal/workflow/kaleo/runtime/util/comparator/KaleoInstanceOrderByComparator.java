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

package com.liferay.portal.workflow.kaleo.runtime.util.comparator;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorAdapter;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

/**
 * @author William Newbury
 */
public class KaleoInstanceOrderByComparator
	extends OrderByComparatorAdapter<KaleoInstance, WorkflowInstance> {

	public static OrderByComparator<KaleoInstance> getOrderByComparator(
		OrderByComparator<WorkflowInstance> orderByComparator,
		KaleoWorkflowModelConverter kaleoWorkflowModelConverter,
		ServiceContext serviceContext) {

		if (orderByComparator == null) {
			return null;
		}

		return new KaleoInstanceOrderByComparator(
			orderByComparator, kaleoWorkflowModelConverter, serviceContext);
	}

	@Override
	public WorkflowInstance adapt(KaleoInstance kaleoInstance) {
		try {
			return _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private KaleoInstanceOrderByComparator(
		OrderByComparator<WorkflowInstance> orderByComparator,
		KaleoWorkflowModelConverter kaleoWorkflowModelConverter,
		ServiceContext serviceContext) {

		super(orderByComparator);

		_kaleoWorkflowModelConverter = kaleoWorkflowModelConverter;
		_serviceContext = serviceContext;
	}

	private final KaleoWorkflowModelConverter _kaleoWorkflowModelConverter;
	private final ServiceContext _serviceContext;

}