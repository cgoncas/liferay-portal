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

package com.liferay.arquillian.extension.internal.observer;

import com.liferay.arquillian.junit.JUnitHelperUtil;
import com.liferay.portal.kernel.test.BaseTestRule;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.ClassEvent;
import org.jboss.arquillian.test.spi.event.suite.Test;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Shuyang Zhou
 */
public class JunitTestRuleBridgeObserver {

	public void afterClass(@Observes EventContext<AfterClass> eventContext)
		throws Throwable {

		eventContext.proceed();

		Description description = _toDescription(eventContext.getEvent());

		List<TestRule> testRules = JUnitHelperUtil.getClassTestRules();

		for (int i = 0; i < testRules.size(); i++) {
			TestRule testRule = testRules.get(i);

			if (!(testRule instanceof BaseTestRule)) {
				throw new IllegalArgumentException(
					testRule.getClass() + " is not " + BaseTestRule.class);
			}

			BaseTestRule<Object, ?> baseTestRule =
				(BaseTestRule<Object, ?>)testRule;

			baseTestRule.afterClass(description, _objects.get(i));
		}
	}

	public void beforeClass(@Observes EventContext<BeforeClass> eventContext)
		throws Throwable {

		Description description = _toDescription(eventContext.getEvent());

		List<TestRule> testRules = JUnitHelperUtil.getClassTestRules();

		_objects = new ArrayList<Object>(testRules.size());

		for (int i = testRules.size() - 1; i >= 0; i--) {
			TestRule testRule = testRules.get(i);

			if (!(testRule instanceof BaseTestRule)) {
				throw new IllegalArgumentException(
					testRule.getClass() + " is not " + BaseTestRule.class);
			}

			BaseTestRule<Object, ?> baseTestRule =
				(BaseTestRule<Object, ?>)testRule;

			_objects.add(baseTestRule.beforeClass(description));
		}

		eventContext.proceed();
	}

	public void test(@Observes final EventContext<Test> eventContext)
		throws Throwable {

		Test test = eventContext.getEvent();

		Description description = _toDescription(test);

		Statement statement = new InvokeMethod(null, test.getTestInstance()) {

			@Override
			public void evaluate() {
				eventContext.proceed();
			}

		};

		Object target = test.getTestInstance();

		statement = JUnitHelperUtil.wrapBefores(statement, target);
		statement = JUnitHelperUtil.wrapAfters(statement, target);

		List<TestRule> testRules = JUnitHelperUtil.getMethodTestRules(target);

		for (TestRule testRule : testRules) {
			statement = testRule.apply(statement, description);
		}

		statement.evaluate();
	}

	private Description _toDescription(ClassEvent classEvent) {
		TestClass testClass = classEvent.getTestClass();

		return Description.createSuiteDescription(testClass.getJavaClass());
	}

	private Description _toDescription(TestEvent testEvent) {
		TestClass testClass = testEvent.getTestClass();

		Method method = testEvent.getTestMethod();

		return Description.createTestDescription(
			testClass.getJavaClass(), method.getName());
	}

	private List<Object> _objects;

}