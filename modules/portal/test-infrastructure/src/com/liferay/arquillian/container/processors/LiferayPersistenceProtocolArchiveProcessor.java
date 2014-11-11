/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.arquillian.container.processors;

import com.liferay.arquillian.processor.OsgiManifestUtil;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * @author Carlos Sierra Andrés
 */
public class LiferayPersistenceProtocolArchiveProcessor
	implements ProtocolArchiveProcessor {

	public static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	@Override
	public void process(
		TestDeployment testDeployment, Archive<?> protocolArchive) {

		Archive<?> applicationArchive = testDeployment.getApplicationArchive();

		Manifest manifest = OsgiManifestUtil.findOrCreateManifest(
			applicationArchive);

		OsgiManifestUtil.ensureProperManifest(
			manifest, testDeployment.getDeploymentName());

		OsgiManifestUtil osgiManifestUtil = OsgiManifestUtil.create(manifest);

		osgiManifestUtil.appendToImport("org.springframework.transaction");

		osgiManifestUtil.appendToImport(
			"org.springframework.transaction.interceptor");
		osgiManifestUtil.appendToImport(
			"com.liferay.portal.cache.transactional");
		osgiManifestUtil.appendToImport("com.liferay.portal.model");
		osgiManifestUtil.appendToImport("com.liferay.portal.kernel.bean");
		osgiManifestUtil.appendToImport("com.liferay.portal.kernel.dao.orm");
		osgiManifestUtil.appendToImport("com.liferay.portal.kernel.test");
		osgiManifestUtil.appendToImport("com.liferay.portal.kernel.util");
		osgiManifestUtil.appendToImport(
			"com.liferay.portal.spring.transaction");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			osgiManifestUtil.writeTo(baos);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteArrayAsset byteArrayAsset = new ByteArrayAsset(baos.toByteArray());

		replaceManifest(applicationArchive, byteArrayAsset);
	}

	private void replaceManifest(
		Archive<?> archive, ByteArrayAsset byteArrayAsset) {

		archive.delete(MANIFEST_PATH);

		archive.add(byteArrayAsset, MANIFEST_PATH);
	}

}
