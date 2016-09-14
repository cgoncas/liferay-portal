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
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * @author Manuel de la Peña
 */
public class EndpointWrapper {

	public EndpointWrapper(Endpoint endpoint) {
		_endpoint = endpoint;
	}

	public List<Session> getSessions() {
		return _sessions;
	}

	public void onClose(Session session, CloseReason closeReason) {
		_endpoint.onClose(session, closeReason);

		_sessions.remove(session);
	}

	public void onOpen(Session session, EndpointConfig config) {
		_sessions.add(session);

		_endpoint.onOpen(session, config);
	}

	private final Endpoint _endpoint;
	private final List<Session> _sessions = new CopyOnWriteArrayList<>();

}