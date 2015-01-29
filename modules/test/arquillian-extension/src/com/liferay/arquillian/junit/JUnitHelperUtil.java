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

import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

/**
 * @author Shuyang Zhou
 */
public class JUnitHelperUtil {

	public static List<TestRule> getClassTestRules() {
		return _testRuleFactory.getClassTestRules();
	}

	public static List<TestRule> getMethodTestRules(Object target) {
		return _testRuleFactory.getMethodTestRules(target);
	}

	public static void setTestRuleFactory(JUnitHelper testRuleFactory) {
		_testRuleFactory = testRuleFactory;
	}

	public static Statement wrapAfters(Statement statement, Object target) {
		return _testRuleFactory.wrapAfters(statement, target);
	}

	public static Statement wrapBefores(Statement statement, Object target) {
		return _testRuleFactory.wrapBefores(statement, target);
	}

	private static JUnitHelper _testRuleFactory;

}