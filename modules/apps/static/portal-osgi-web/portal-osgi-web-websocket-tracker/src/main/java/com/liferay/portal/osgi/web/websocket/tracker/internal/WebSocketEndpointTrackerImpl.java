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

package com.liferay.portal.osgi.web.websocket.tracker.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.osgi.web.websocket.tracker.EndpointWrapper;
import com.liferay.portal.osgi.web.websocket.tracker.WebSocketEndpointTracker;

import java.io.IOException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig.Configurator;

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

		service.setConfigurator(_NULL);

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

	private static final Configurator _NULL = new Configurator() {

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getEndpointInstance(Class<T> endpointClass) {
			return (T)new NullEndpoint();
		}

	};

	private BundleContext _bundleContext;
	private final ConcurrentMap<String, EndpointWrapper>
		_webSocketEndpointRegistrations = new ConcurrentHashMap<>();
	private ServiceTracker<Endpoint, EndpointWrapper>
		_webSocketEndpointServiceTracker;

	private static class NullEndpoint extends Endpoint {

		@Override
		public void onOpen(Session session, EndpointConfig config) {
			try {
				session.close(
					new CloseReason(
						CloseReason.CloseCodes.GOING_AWAY,
						"Service has gone away"));
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}

}