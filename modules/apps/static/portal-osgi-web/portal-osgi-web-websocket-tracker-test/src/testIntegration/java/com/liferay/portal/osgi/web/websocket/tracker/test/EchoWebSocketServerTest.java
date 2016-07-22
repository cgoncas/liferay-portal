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

package com.liferay.portal.osgi.web.websocket.tracker.test;

import com.liferay.portal.osgi.web.websocket.tracker.test.client.EchoWebSocketClient;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class EchoWebSocketServerTest {

	@RunAsClient
	@Test
	public void testConectToEndpoint() throws Exception {
		String dest = "ws://localhost:8080/o/websocket/echo";

		EchoWebSocketClient socket = new EchoWebSocketClient();

		WebSocketContainer container =
			ContainerProvider.getWebSocketContainer();

		container.connectToServer(socket, new URI(dest));

		socket.getLatch().await();
		socket.sendMessage("Echo");

		socket.sendMessage("Test");

		Thread.sleep(10000L);

		Assert.assertEquals("Test", socket.popReceivedMessages());
		Assert.assertEquals("Echo", socket.popReceivedMessages());
	}

}