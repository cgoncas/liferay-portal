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

package com.liferay.portal.users.rest.config;

import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Carlos Sierra Andrés
 */
@Component(immediate = true, service = ConfigurationComponent.class)
public class ConfigurationComponent {

	@Activate
	public void activate(BundleContext bundleContext) throws IOException {
		_cxfConfiguration = _configurationAdmin.createFactoryConfiguration(
			"com.liferay.portal.ws.WebServicePublisherConfiguration", null);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("contextPath", "/rest");

		_cxfConfiguration.update(properties);

		_restConfiguration = _configurationAdmin.createFactoryConfiguration(
			"com.liferay.portal.rest.extender.RestExtenderConfiguration", null);

		properties = new Hashtable<>();

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "rest");
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME,
			"AuthVerifierFilter");
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/*");

		_authVerifierFilterServiceRegistration = bundleContext.registerService(
			Filter.class, new AuthVerifierFilter(), properties);

		properties = new Hashtable<>();

		properties.put("contextPaths", new String[] {"/rest"});
		properties.put(
			"applicationFilters", new String[] {"(jaxrs.application=true)"});

		_restConfiguration.update(properties);
	}

	@Deactivate
	public void deactivate(BundleContext bundleContext) throws IOException {
		_restConfiguration.delete();

		_authVerifierFilterServiceRegistration.unregister();

		_cxfConfiguration.delete();
	}

	@Reference
	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	private ServiceRegistration<Filter> _authVerifierFilterServiceRegistration;
	private ConfigurationAdmin _configurationAdmin;
	private Configuration _cxfConfiguration;
	private Configuration _restConfiguration;

}