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

package com.liferay.portal.osgi.web.websocket.helper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.CloseReason;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author Manuel de la Peña
 */
public class EndpointWrapper implements ServerEndpointConfig {

	public EndpointWrapper(String path, Endpoint endpoint) {
		_serverEndpointConfig = Builder.create(Endpoint.class, path).build();

		_configurator = _serverEndpointConfig.getConfigurator();

		_endpoint = endpoint;
	}

	@Override
	public Configurator getConfigurator() {
		return _configurator;
	}

	@Override
	public List<Class<? extends Decoder>> getDecoders() {
		return _serverEndpointConfig.getDecoders();
	}

	@Override
	public List<Class<? extends Encoder>> getEncoders() {
		return _serverEndpointConfig.getEncoders();
	}

	@Override
	public Class<?> getEndpointClass() {
		return _serverEndpointConfig.getEndpointClass();
	}

	@Override
	public List<Extension> getExtensions() {
		return _serverEndpointConfig.getExtensions();
	}

	@Override
	public String getPath() {
		return _serverEndpointConfig.getPath();
	}

	public List<Session> getSessions() {
		return _sessions;
	}

	@Override
	public List<String> getSubprotocols() {
		return _serverEndpointConfig.getSubprotocols();
	}

	@Override
	public Map<String, Object> getUserProperties() {
		return _serverEndpointConfig.getUserProperties();
	}

	public void onClose(Session session, CloseReason closeReason) {
		_endpoint.onClose(session, closeReason);

		_sessions.remove(session);
	}

	public void onOpen(Session session, EndpointConfig config) {
		_sessions.add(session);

		_endpoint.onOpen(session, config);
	}

	public void setConfigurator(Configurator configurator) {
		_configurator = configurator;
	}

	private Configurator _configurator;
	private final Endpoint _endpoint;
	private final ServerEndpointConfig _serverEndpointConfig;
	private final List<Session> _sessions = new CopyOnWriteArrayList<>();

}