/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.arquillian.extension.portal.test.internal;


import com.liferay.arquillian.extension.portal.test.internal.deployment.PortalTestInternalAuxiliaryArchiveAppender;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;

/**
 * @author Cristina González
 */
public class LiferayArquillianPortalTestInternalExtension
	implements RemoteLoadableExtension {

	@Override
	public void register(ExtensionBuilder extensionBuilder) {
		extensionBuilder.service(
			AuxiliaryArchiveAppender.class,
			PortalTestInternalAuxiliaryArchiveAppender.class);
	}

}
