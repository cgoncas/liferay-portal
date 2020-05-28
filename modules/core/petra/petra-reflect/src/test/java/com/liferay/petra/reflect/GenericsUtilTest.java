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

package com.liferay.petra.reflect;

import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class GenericsUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructor() {
		new GenericsUtil();
	}

	@Test
	public void testGetGenericClassNameWithNoParameterizedTypeObject() {
		Assert.assertEquals(
			Object.class.getCanonicalName(),
			GenericsUtil.getGenericClassName(new NoParameterizedTypeImpl()));
	}

	@Test
	public void testGetGenericClassNameWithParameterizedTypeObject() {
		Assert.assertEquals(
			String.class.getCanonicalName(),
			GenericsUtil.getGenericClassName(new StringParameterizedType()));
	}

	@Test
	public void testGetGenericClassWithNoParameterizedTypeChildClass() {
		Assert.assertEquals(
			Object.class,
			GenericsUtil.getGenericClass(NoParameterizedTypeImplChild.class));
	}

	@Test
	public void testGetGenericClassWithNoParameterizedTypeClass() {
		Assert.assertEquals(
			Object.class,
			GenericsUtil.getGenericClass(NoParameterizedTypeImpl.class));
	}

	@Test
	public void testGetGenericClassWithNoParameterizedTypeObject() {
		Assert.assertEquals(
			Object.class,
			GenericsUtil.getGenericClass(new NoParameterizedTypeImpl()));
	}

	@Test
	public void testGetGenericClassWithParameterizedTypeClass() {
		Assert.assertEquals(
			String.class,
			GenericsUtil.getGenericClass(StringParameterizedType.class));
	}

	@Test
	public void testGetGenericClassWithParameterizedTypeObject() {
		Assert.assertEquals(
			String.class,
			GenericsUtil.getGenericClass(new StringParameterizedType()));
	}

	public static class NoParameterizedTypeImpl implements NoParameterizedType {
	}

	public static class NoParameterizedTypeImplChild
		extends NoParameterizedTypeImpl {
	}

	public static class StringParameterizedType
		implements ParameterizedType<String> {
	}

	public interface NoParameterizedType {
	}

	public interface ParameterizedType<T> {
	}

}