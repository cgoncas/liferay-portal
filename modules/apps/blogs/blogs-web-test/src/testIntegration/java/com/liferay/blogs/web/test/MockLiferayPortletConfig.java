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

package com.liferay.blogs.web.test;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import javax.xml.namespace.QName;

import org.springframework.mock.web.portlet.MockPortletConfig;

/**
 * @author Cristina González
 * @author Alicia García
 */
public class MockLiferayPortletConfig
	extends MockPortletConfig implements LiferayPortletConfig {

	@Override
	public Portlet getPortlet() {
		return null;
	}

	@Override
	public String getPortletId() {
		return "testPortlet";
	}

	@Override
	public Enumeration<PortletMode> getPortletModes(String mimeType) {
		return null;
	}

	@Override
	public Map<String, QName> getPublicRenderParameterDefinitions() {
		return null;
	}

	@Override
	public Enumeration<WindowState> getWindowStates(String mimeType) {
		return null;
	}

	@Override
	public boolean isCopyRequestParameters() {
		return false;
	}

	@Override
	public boolean isWARFile() {
		return false;
	}

}