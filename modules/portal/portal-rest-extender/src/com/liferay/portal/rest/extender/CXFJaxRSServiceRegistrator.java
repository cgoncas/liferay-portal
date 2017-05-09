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

package com.liferay.portal.rest.extender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;

/**
* @author Carlos Sierra Andrés
*/
class CXFJaxRSServiceRegistrator {

	public CXFJaxRSServiceRegistrator(Map<String, Object> properties) {

		_properties = properties;
	}

	public synchronized void addApplication(Application application) {
		_applications.add(application);

		rewire();
	}

	public synchronized void addBus(Bus bus) {
		_buses.add(bus);

		for (Application application : _applications) {
			registerApplicationInBus(bus, application);
		}
	}

	public synchronized void addProvider(Object provider) {
		_providers.add(provider);

		rewire();
	}

	public synchronized void addService(Object service) {
		_services.add(service);

		rewire();
	}

	public synchronized void removeApplication(Application application) {
		_applications.remove(application);

		removeFromBuses(application);
	}

	public synchronized void removeProvider(Object provider) {
		_providers.remove(provider);

		rewire();
	}

	public synchronized void removeService(Object service) {
		_services.remove(service);

		rewire();
	}

	private void registerApplicationInBus(Bus bus, Application application) {
		RuntimeDelegate runtimeDelegate = RuntimeDelegate.getInstance();

		JAXRSServerFactoryBean jaxrsServerFactoryBean =
			runtimeDelegate.createEndpoint(
				application, JAXRSServerFactoryBean.class);

		jaxrsServerFactoryBean.setProperties(_properties);

		jaxrsServerFactoryBean.setBus(bus);

		JSONProvider<Object> jsonProvider = new JSONProvider<>();

		jsonProvider.setDropRootElement(true);
		jsonProvider.setDropCollectionWrapperElement(true);
		jsonProvider.setSerializeAsArray(true);
		jsonProvider.setSupportUnwrapped(true);

		jaxrsServerFactoryBean.setProvider(jsonProvider);

		List<Object> providers = new ArrayList<>(_providers);

		providers.addAll(_providers);
		providers.addAll(jaxrsServerFactoryBean.getProviders());

		jaxrsServerFactoryBean.setProviders(providers);

		for (Object service : _services) {
			jaxrsServerFactoryBean.setServiceBean(service);
		}

		Server server = jaxrsServerFactoryBean.create();

		server.start();

		storeInBus(bus, application, server);
	}

	private void registerApplicationsInBuses() {
		for (Bus bus : _buses) {
			for (Application application : _applications) {
				registerApplicationInBus(bus, application);
			}
		}
	}

	private void rewire() {
		for (Application application : _applications) {
			removeFromBuses(application);
		}

		registerApplicationsInBuses();
	}

	private final Collection<Application> _applications = new ArrayList<>();
	private final Map<String, Object> _properties;
	private final List<Object> _providers = new ArrayList<>();
	private final Collection<Object> _services = new ArrayList<>();
	private final Map<Bus, Map<Object, Server>> _serversPerBus =
		Collections.synchronizedMap(
			new IdentityHashMap<Bus, Map<Object, Server>>());
	private final Collection<Bus> _buses = new ArrayList<>();

	protected void removeFromBuses(Object application) {
		synchronized (_serversPerBus) {
			for (Map<Object, Server> servers : _serversPerBus.values()) {
				Server server = servers.remove(application);

				if (server != null) {
					server.destroy();
				}
			}
		}
	}

	protected void storeInBus(Bus bus, Object object, Server server) {
		synchronized (_serversPerBus) {
			Map<Object, Server> servers = _serversPerBus.get(bus);

			if (servers == null) {
				servers = new HashMap<>();

				_serversPerBus.put(bus, servers);
			}

			servers.put(object, server);
		}
	}

	public synchronized void removeBus(Bus bus) {
		_buses.remove(bus);

		synchronized (_serversPerBus) {
			Map<Object, Server> servers = _serversPerBus.remove(bus);

			if (servers == null) {
				return;
			}

			for (Server server : servers.values()) {
				server.destroy();
			}
		}
	}
}