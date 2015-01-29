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

package com.liferay.arquillian.junit;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * @author Shuyang Zhou
 */
public class Arquillian extends org.jboss.arquillian.junit.Arquillian
	implements JUnitHelper {

	public Arquillian(Class<?> clazz) throws InitializationError {
		super(clazz);

		JUnitHelperUtil.setTestRuleFactory(this);
	}

	@Override
	public List<TestRule> getClassTestRules() {
		return super.classRules();
	}

	@Override
	public List<TestRule> getMethodTestRules(Object target) {
		return super.getTestRules(target);
	}

	@Override
	public Statement wrapAfters(Statement statement, Object target) {
		TestClass testClass = getTestClass();

		List<FrameworkMethod> frameworkMethods = testClass.getAnnotatedMethods(
			After.class);

		if (!frameworkMethods.isEmpty()) {
			statement = new RunAfters(statement, frameworkMethods, target);
		}

		return statement;
	}

	@Override
	public Statement wrapBefores(Statement statement, Object target) {
		TestClass testClass = getTestClass();

		List<FrameworkMethod> frameworkMethods = testClass.getAnnotatedMethods(
			Before.class);

		if (!frameworkMethods.isEmpty()) {
			statement = new RunBefores(statement, frameworkMethods, target);
		}

		return statement;
	}

	@Override
	protected List<TestRule> classRules() {
		return Collections.emptyList();
	}

	@Override
	protected List<TestRule> getTestRules(Object target) {
		return Collections.emptyList();
	}

	@Override
	protected Statement withAfters(
		FrameworkMethod faFrameworkMethod, Object target, Statement statement) {

		return statement;
	}

	@Override
	protected Statement withBefores(
		FrameworkMethod faFrameworkMethod, Object target, Statement statement) {

		return statement;
	}

}