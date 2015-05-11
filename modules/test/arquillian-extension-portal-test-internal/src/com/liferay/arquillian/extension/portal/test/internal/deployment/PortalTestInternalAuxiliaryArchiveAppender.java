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

package com.liferay.arquillian.extension.portal.test.internal.deployment;

import com.liferay.portal.kernel.util.CharPool;

import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import java.io.File;

import java.net.URL;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author Shuyang Zhou
 */
public class PortalTestInternalAuxiliaryArchiveAppender
	implements AuxiliaryArchiveAppender {

	@Override
	public Archive<?> createAuxiliaryArchive() {

		File portalTestInternalJarFile =
			getJarFile(LiferayIntegrationTestRule.class);

		long statTime = System.currentTimeMillis();

		JavaArchive archive = ShrinkWrap.create(
			ZipImporter.class, "arquillian-extension-portal-test-internal.jar").
			importFrom(portalTestInternalJarFile).as(JavaArchive.class);

		archive.addClass(PortalTestInternalAuxiliaryArchiveAppender.class);

		long endTime = System.currentTimeMillis();

		System.out.println("TIME: " + (endTime - statTime));


		return archive;
	}

	protected File getJarFile(Class<?> clazz) {
		String className = clazz.getName();

		String resourceName = className.replace(
			CharPool.PERIOD, CharPool.SLASH);

		ClassLoader classLoader = clazz.getClassLoader();

		URL url = classLoader.getResource(resourceName.concat(".class"));

		String file = url.getFile();

		if (file.startsWith("file:")) {
			file = file.substring("file:".length());
		}

		int index = file.lastIndexOf(CharPool.EXCLAMATION);

		if (index != -1) {
			file = file.substring(0, index);
		}

		return new File(file);
	}

}