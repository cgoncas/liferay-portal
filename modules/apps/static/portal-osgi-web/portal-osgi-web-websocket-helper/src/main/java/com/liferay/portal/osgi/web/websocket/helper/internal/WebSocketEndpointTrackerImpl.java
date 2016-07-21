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

package com.liferay.portal.osgi.web.websocket.helper.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.osgi.web.websocket.helper.EndpointWrapper;
import com.liferay.portal.osgi.web.websocket.helper.WebSocketEndpointTracker;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.Session;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Manuel de la Peña
 */
@Component(immediate = true, service = WebSocketEndpointTracker.class)
public class WebSocketEndpointTrackerImpl
	implements ServiceTrackerCustomizer<Endpoint, EndpointWrapper>,
			   WebSocketEndpointTracker {

	@Override
	public EndpointWrapper addingService(
		ServiceReference<Endpoint> serviceReference) {

		Endpoint endpoint = _bundleContext.getService(serviceReference);

		String webSocketPath = (String)serviceReference.getProperty(
			"websocket.path");

		if ((webSocketPath == null) || webSocketPath.isEmpty()) {
			return null;
		}

		EndpointWrapper endpointWrapper = new EndpointWrapper(
			webSocketPath, endpoint);

		_webSocketEndpointRegistrations.put(webSocketPath, endpointWrapper);

		return endpointWrapper;
	}

	@Override
	public EndpointWrapper getEndpoint(String path) {
		return _webSocketEndpointRegistrations.get(path);
	}

	@Override
	public void modifiedService(
		ServiceReference<Endpoint> serviceReference,
		EndpointWrapper endpointWrapper) {

		removedService(serviceReference, endpointWrapper);

		addingService(serviceReference);
	}

	@Override
	public void removedService(
		ServiceReference<Endpoint> serviceReference, EndpointWrapper service) {

		String webSocketPath = (String)serviceReference.getProperty(
			"websocket.path");

		List<Session> sessions = service.getSessions();

		CloseReason closeReason = new CloseReason(
			CloseReason.CloseCodes.GOING_AWAY,
			"The WebSocket Connection has gone away");

		for (Session session : sessions) {
			service.onClose(session, closeReason);
		}

		_webSocketEndpointRegistrations.remove(webSocketPath);

		_bundleContext.ungetService(serviceReference);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (_webSocketEndpointServiceTracker != null) {
			_webSocketEndpointServiceTracker.close();
		}

		_bundleContext = bundleContext;

		_webSocketEndpointServiceTracker = ServiceTrackerFactory.open(
			_bundleContext, Endpoint.class, this);
	}

	@Deactivate
	protected void deactivate() {
		_webSocketEndpointServiceTracker.close();

		_webSocketEndpointServiceTracker = null;

		_bundleContext = null;
	}

	private BundleContext _bundleContext;
	private final ConcurrentMap<String, EndpointWrapper>
		_webSocketEndpointRegistrations = new ConcurrentHashMap<>();
	private ServiceTracker<Endpoint, EndpointWrapper>
		_webSocketEndpointServiceTracker;

}