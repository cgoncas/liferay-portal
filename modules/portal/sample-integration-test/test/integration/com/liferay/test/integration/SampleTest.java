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

package com.liferay.test.integration;

import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.util.PortalUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
@Transactional
public class SampleTest {

	@Deployment
	public static WebArchive getDeployment() {
		return ShrinkWrap.create(WebArchive.class);
	}

	@Test
	public void testTest() {
		Assert.assertTrue(PortalUtil.getDefaultCompanyId() > 0);
	}
}
