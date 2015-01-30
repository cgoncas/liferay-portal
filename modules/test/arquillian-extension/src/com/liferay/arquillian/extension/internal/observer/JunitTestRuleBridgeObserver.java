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

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author Shuyang Zhou
 */
public class JunitTestRuleBridgeObserver {

	public void afterClass(@Observes EventContext<AfterClass> eventContext)
		throws Throwable {

		eventContext.proceed();

		Description description = _toDescription(eventContext.getEvent());

		for (int i = 0; i < _classRules.size(); i++) {
			TestRule testRule = _classRules.get(i);

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

		ClassEvent classEvent = eventContext.getEvent();

		Description description = _toDescription(classEvent);

		TestClass testClass = classEvent.getTestClass();

		List<TestRule> testRules = getClassRules(testClass.getJavaClass());

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

		Statement statement = new InvokeMethod(null, test.getTestInstance()) {

			@Override
			public void evaluate() {
				eventContext.proceed();
			}

		};

		statement = _withBefores(statement, test);
		statement = _withAfters(statement, test);
		statement = _withRules(statement, test);

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

	private org.junit.runners.model.TestClass _toTestClass(Test test) {
		TestClass testClass = test.getTestClass();

		return new org.junit.runners.model.TestClass(testClass.getJavaClass());
	}

	private Statement _withAfters(Statement statement, Test test) {
		org.junit.runners.model.TestClass testClass = _toTestClass(test);

		List<FrameworkMethod> afterFrameworkMethods =
			testClass.getAnnotatedMethods(After.class);

		if (!afterFrameworkMethods.isEmpty()) {
			statement = new RunAfters(
				statement, afterFrameworkMethods, test.getTestInstance());
		}

		return statement;
	}

	private Statement _withBefores(Statement statement, Test test) {
		org.junit.runners.model.TestClass testClass = _toTestClass(test);

		List<FrameworkMethod> beforeFrameworkMethods =
			testClass.getAnnotatedMethods(Before.class);

		if (!beforeFrameworkMethods.isEmpty()) {
			statement = new RunBefores(
				statement, beforeFrameworkMethods, test.getTestInstance());
		}

		return statement;
	}

	private Statement _withRules(Statement statement, Test test) {
		org.junit.runners.model.TestClass testClass = _toTestClass(test);

		List<TestRule> testRules = testClass.getAnnotatedMethodValues(
			test.getTestInstance(), Rule.class, TestRule.class);

		testRules.addAll(
			testClass.getAnnotatedFieldValues(
				test.getTestInstance(), Rule.class, TestRule.class));

		Description description = _toDescription(test);

		for (TestRule testRule : testRules) {
			statement = testRule.apply(statement, description);
		}

		return statement;
	}

	private List<TestRule> getClassRules(Class<?> clazz) {
		org.junit.runners.model.TestClass testClass =
			new org.junit.runners.model.TestClass(clazz);

		_classRules = testClass.getAnnotatedMethodValues(
			null, ClassRule.class, TestRule.class);

		_classRules.addAll(
			testClass.getAnnotatedFieldValues(
				null, ClassRule.class, TestRule.class));

		return _classRules;
	}

	private List<TestRule> _classRules;
	private List<Object> _objects;

}