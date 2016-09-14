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

package com.liferay.portal.osgi.web.websocket.tracker.test.client;

import java.io.IOException;

import java.util.Stack;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * @author Cristina González
 */
@ClientEndpoint
public class EchoWebSocketClient {

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		System.out.println(
			"Closing a WebSocket due to " + reason.getReasonPhrase());
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connected to server");
		_session = session;
	}

	@OnMessage
	public void onText(String message, Session session) {
		_receivedMessages.add(message);
	}

	public String popReceivedMessages() {
		return _receivedMessages.pop();
	}

	public void sendMessage(String str) {
		try {
			_session.getBasicRemote().sendText(str);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	private final Stack<String> _receivedMessages = new Stack();
	private Session _session;

}