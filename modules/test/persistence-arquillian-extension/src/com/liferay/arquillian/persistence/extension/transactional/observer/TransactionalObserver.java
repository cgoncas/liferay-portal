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

package com.liferay.arquillian.persistence.extension.transactional.observer;

import com.liferay.arquillian.persistence.extension.transactional.util.TransactionalUtil;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.Test;

/**
 * @author Cristina González
 */
public class TransactionalObserver {

	public void after(@Observes final EventContext<After> eventContext)
		throws Throwable {

		TransactionalUtil transactionalUtil = _transactionalUtilInstance.get();

		transactionalUtil.callAsTransactional(eventContext);
	}

	public void before(@Observes final EventContext<Before> eventContext)
		throws Throwable {

		TransactionalUtil transactionalUtil = _transactionalUtilInstance.get();

		transactionalUtil.callAsTransactional(eventContext);
	}

	public void test(@Observes final EventContext<Test> eventContext)
		throws Throwable {

		TransactionalUtil transactionalUtil = _transactionalUtilInstance.get();

		transactionalUtil.callAsTransactional(eventContext);
	}

	@Inject
	private Instance<TransactionalUtil> _transactionalUtilInstance;

}