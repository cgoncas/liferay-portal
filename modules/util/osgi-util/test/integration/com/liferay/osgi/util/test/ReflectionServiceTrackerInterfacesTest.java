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

package com.liferay.osgi.util.test;

import com.liferay.arquillian.deploymentscenario.annotations.BndFile;
import com.liferay.osgi.util.service.ReflectionServiceTracker;
import com.liferay.osgi.util.service.UnavailableServiceException;
import com.liferay.osgi.util.test.instances.TestInterface;
import com.liferay.osgi.util.test.services.InterfaceOne;
import com.liferay.osgi.util.test.services.InterfaceTwo;
import com.liferay.osgi.util.test.services.TrackedOne;
import com.liferay.osgi.util.test.services.TrackedTwo;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Hashtable;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Sierra Andrés
 */
@BndFile("test/integration/test-bnd.bnd")
@RunWith(Arquillian.class)
public class ReflectionServiceTrackerInterfacesTest {

	@Before
	public void setUp() throws BundleException {
		_bundle.start();

		_bundleContext = _bundle.getBundleContext();
	}

	@After
	public void tearDown() throws BundleException {
		_bundle.stop();
	}

	@Test
	public void testReflectionServiceTracker() throws IOException {
		TestInterface testInterface = new TestInterface();

		ReflectionServiceTracker reflectionServiceTracker =
			new ReflectionServiceTracker(testInterface);

		TrackedOne trackedOne = new TrackedOne();
		ServiceRegistration<InterfaceOne> sr1 = _bundleContext.registerService(
			InterfaceOne.class, trackedOne, null);

		TrackedTwo trackedTwo = new TrackedTwo();
		ServiceRegistration<InterfaceTwo> sr2 = _bundleContext.registerService(
			InterfaceTwo.class, trackedTwo, null);

		Assert.assertEquals(trackedOne, testInterface.getTrackedOne());
		Assert.assertEquals(trackedTwo, testInterface.getTrackedTwo());

		sr1.unregister();
		sr2.unregister();

		reflectionServiceTracker.close();
	}

	@Test
	public void testReflectionServiceTrackerInitialConditions() {
		TestInterface testInterface = new TestInterface();

		ReflectionServiceTracker reflectionServiceTracker =
			new ReflectionServiceTracker(testInterface);

		Assert.assertNotNull(testInterface.getTrackedOne());
		Assert.assertNotNull(testInterface.getTrackedTwo());

		try {
			testInterface.getTrackedOne().noop();

			Assert.fail();
		}
		catch (UnavailableServiceException sue) {
			Assert.assertEquals(
				InterfaceOne.class, sue.getUnavailableServiceClass());
		}

		try {
			testInterface.getTrackedTwo().noop2();

			Assert.fail();
		}
		catch (UnavailableServiceException sue) {
			Assert.assertEquals(
				InterfaceTwo.class, sue.getUnavailableServiceClass());
		}

		reflectionServiceTracker.close();
	}

	@Test
	public void testReflectionServiceTrackerUnregistration() {
		TestInterface testInterface = new TestInterface();

		ReflectionServiceTracker reflectionServiceTracker =
			new ReflectionServiceTracker(testInterface);

		Assert.assertNotNull(testInterface.getTrackedOne());
		Assert.assertNotNull(testInterface.getTrackedTwo());

		TrackedOne trackedOne = new TrackedOne();
		ServiceRegistration<InterfaceOne> sr1 = registerService(
			InterfaceOne.class, trackedOne, 0);

		TrackedTwo trackedTwo = new TrackedTwo();
		ServiceRegistration<InterfaceTwo> sr2 = registerService(
			InterfaceTwo.class, trackedTwo, 0);

		Assert.assertEquals(trackedOne, testInterface.getTrackedOne());
		Assert.assertEquals(trackedTwo, testInterface.getTrackedTwo());

		sr1.unregister();
		sr2.unregister();

		Assert.assertNotNull(testInterface.getTrackedOne());
		Assert.assertNotNull(testInterface.getTrackedTwo());

		reflectionServiceTracker.close();
	}

	@Test
	public void testReflectionServiceTrackerWithModifiedService()
		throws IOException {

		TestInterface testInterface = new TestInterface();

		ReflectionServiceTracker reflectionServiceTracker =
			new ReflectionServiceTracker(testInterface);

		TrackedOne trackedOne = new TrackedOne();
		ServiceRegistration<InterfaceOne> sr1 = registerService(
			InterfaceOne.class, trackedOne, 2);
		TrackedTwo trackedTwo = new TrackedTwo();
		ServiceRegistration<InterfaceTwo> sr2 = registerService(
			InterfaceTwo.class, trackedTwo, 2);

		TrackedOne trackedOne2 = new TrackedOne();
		ServiceRegistration<InterfaceOne> sr3 = registerService(
			InterfaceOne.class, trackedOne2, 1);
		TrackedTwo trackedTwo2 = new TrackedTwo();
		ServiceRegistration<InterfaceTwo> sr4 = registerService(
			InterfaceTwo.class, trackedTwo2, 1);

		Assert.assertEquals(trackedOne, testInterface.getTrackedOne());
		Assert.assertEquals(trackedTwo, testInterface.getTrackedTwo());

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put("service.ranking", 3);

		sr3.setProperties(properties);
		sr4.setProperties(properties);

		Assert.assertEquals(trackedOne2, testInterface.getTrackedOne());
		Assert.assertEquals(trackedTwo2, testInterface.getTrackedTwo());

		sr1.unregister();
		sr2.unregister();
		sr3.unregister();
		sr4.unregister();

		reflectionServiceTracker.close();
	}

	@Test
	public void testReflectionServiceTrackerWithServiceRanking() {
		TestInterface testInterface = new TestInterface();

		ReflectionServiceTracker reflectionServiceTracker =
			new ReflectionServiceTracker(testInterface);

		TrackedOne trackedOne = new TrackedOne();
		ServiceRegistration<InterfaceOne> sr1 = registerService(
			InterfaceOne.class, trackedOne, 2);

		TrackedTwo trackedTwo = new TrackedTwo();
		ServiceRegistration<InterfaceTwo> sr2 = registerService(
			InterfaceTwo.class, trackedTwo, 2);

		TrackedOne trackedOne2 = new TrackedOne();
		ServiceRegistration<InterfaceOne> sr3 = registerService(
			InterfaceOne.class, trackedOne2, 1);

		TrackedTwo trackedTwo2 = new TrackedTwo();
		ServiceRegistration<InterfaceTwo> sr4 = registerService(
			InterfaceTwo.class, trackedTwo2, 1);

		Assert.assertEquals(trackedOne, testInterface.getTrackedOne());
		Assert.assertEquals(trackedTwo, testInterface.getTrackedTwo());

		sr1.unregister();
		sr2.unregister();

		Assert.assertEquals(trackedOne2, testInterface.getTrackedOne());
		Assert.assertEquals(trackedTwo2, testInterface.getTrackedTwo());

		sr3.unregister();
		sr4.unregister();

		reflectionServiceTracker.close();
	}

	private <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T service, int ranking) {

		Dictionary<String, Integer> properties =
			new Hashtable<String, Integer>();

		properties.put("service.ranking", ranking);

		return _bundleContext.registerService(clazz, service, properties);
	}

	@ArquillianResource
	private Bundle _bundle;

	private BundleContext _bundleContext;

}