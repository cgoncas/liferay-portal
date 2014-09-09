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

package com.liferay.arquillian.persistence.extension.transactional.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.transaction.Transactional;

import java.lang.reflect.Method;

import java.util.concurrent.Callable;

import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.ClassEvent;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

/**
 * @author Cristina González
 */
public class TransactionalUtilImpl implements TransactionalUtil {

	public void callAsTransactional(EventContext eventContext)
		throws Throwable {

		Transactional transactionalAnnotation = getAnnotation(
			eventContext.getEvent());

		callAsTransactional(transactionalAnnotation, eventContext);
	}

	protected void callAsTransactional(
			Transactional transactionalAnnotation,
			final EventContext eventContext)
		throws Throwable {

		if (transactionalAnnotation != null) {
			TransactionAttribute.Builder builder =
				new TransactionAttribute.Builder();

			builder.setPropagation(transactionalAnnotation.propagation());
			builder.setRollbackForClasses(
				PortalException.class, SystemException.class);

			TransactionInvokerUtil.invoke(
				builder.build(), new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						eventContext.proceed();
						return null;
					}
				}
			);
		}
		else {
			eventContext.proceed();
		}
	}

	protected Transactional getAnnotation(ClassEvent testEvent) {
		TestClass testClass = testEvent.getTestClass();

		return testClass.getAnnotation(Transactional.class);
	}

	protected Transactional getAnnotation(Object testEvent) {
		if (testEvent instanceof TestEvent) {
			return getAnnotation((TestEvent)testEvent);
		}
		else if (testEvent instanceof ClassEvent) {
			return getAnnotation((ClassEvent)testEvent);
		}
		else {
			throw new RuntimeException(
				"The type of the event should be TestEvent or ClassEvent");
		}
	}

	protected Transactional getAnnotation(TestEvent testEvent) {
		Transactional transactionalAnnotation = getAnnotation(
			(ClassEvent)testEvent);

		Method testMethod = testEvent.getTestMethod();

		Transactional transactionalAnnotationMethod = testMethod.getAnnotation(
			Transactional.class);

		if (transactionalAnnotationMethod != null) {
			transactionalAnnotation = transactionalAnnotationMethod;
		}

		return transactionalAnnotation;
	}

}