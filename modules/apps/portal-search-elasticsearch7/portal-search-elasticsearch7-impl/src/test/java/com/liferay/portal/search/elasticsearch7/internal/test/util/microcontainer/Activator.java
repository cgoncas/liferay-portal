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

package com.liferay.portal.search.elasticsearch7.internal.test.util.microcontainer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.Objects;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author André de Oliveira
 */
public class Activator {

	public Activator(Object component) {
		_component = component;

		_class = component.getClass();

		_classNode = ASMUtil.getClassNode(component.getClass());
	}

	public void activate() {
		Method method = _findMethodActivate();

		if (method != null) {
			_invokeActivate(method);
		}
	}

	private Method _findMethodActivate() {
		for (Method method : _class.getMethods()) {
			if (Objects.equals(method.getName(), "activate")) {
				return method;
			}
		}

		return null;
	}

	private void _invokeActivate(Method method) {
		try {
			Parameter[] parameters = method.getParameters();

			if (parameters.length == 0) {
				method.invoke(_component);
			}
			else {
				method.invoke(
					_component,
					ComponentPropertyMapUtil.getComponentPropertyMap(
						_classNode));
			}
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	private final Class<? extends Object> _class;
	private final ClassNode _classNode;
	private final Object _component;

}