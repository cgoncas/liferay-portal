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

package com.liferay.portal.upgrade.internal;

import com.liferay.portal.upgrade.internal.ReleaseManager.UpgradeProcessInfo;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Carlos Sierra Andrés
 */
public class ReleaseGraphManagerTest {

	@Test
	public void testFindEndNode() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up1, up2, up3, up4));

		String endNode = releaseGraphManager.findEndNode();

		Assert.assertEquals("200", endNode);
	}

	@Test(expected = IllegalStateException.class)
	public void testFindEndNodeWithMultipleEndNodes() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);
		UpgradeProcessInfo up5 = new UpgradeProcessInfo("1.0.0", "2.2.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up1, up2, up3, up4, up5));

		releaseGraphManager.findEndNode();
	}

	@Test
	public void testGetUpgradePathNotInOrder() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up4, up2, up1, up3));

		List<UpgradeProcessInfo> upgradePath =
			releaseGraphManager.getUpgradePath("0", "200");

		Assert.assertEquals(Arrays.asList(up1, up2, up3, up4), upgradePath);
	}

	@Test
	public void testGetUpgradePathWithCyclesReturnsShortestPath() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);
		UpgradeProcessInfo up5 = new UpgradeProcessInfo("0.0.0", "2.0.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up1, up2, up3, up4, up5));

		List<UpgradeProcessInfo> upgradePath =
			releaseGraphManager.getUpgradePath("0", "200");

		Assert.assertEquals(Arrays.asList(up5), upgradePath);
	}

	@Test
	public void testGetUpgradePathWithCyclesReturnsShortestPathWhenNotZero() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);
		UpgradeProcessInfo up5 = new UpgradeProcessInfo("0.0.0", "2.0.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up1, up2, up3, up4, up5));

		List<UpgradeProcessInfo> upgradePath =
			releaseGraphManager.getUpgradePath("10", "200");

		Assert.assertEquals(Arrays.asList(up2, up3, up4), upgradePath);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUpgradePathWithIllegalArguments() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up1, up2, up3, up4));

		releaseGraphManager.getUpgradePath("0", "201");
	}

	@Test
	public void testGetUpgragePath() {
		UpgradeProcessInfo up1 = new UpgradeProcessInfo("0.0.0", "0.1.0", null);
		UpgradeProcessInfo up2 = new UpgradeProcessInfo("0.1.0", "0.2.0", null);
		UpgradeProcessInfo up3 = new UpgradeProcessInfo("0.2.0", "1.0.0", null);
		UpgradeProcessInfo up4 = new UpgradeProcessInfo("1.0.0", "2.0.0", null);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			Arrays.asList(up1, up2, up3, up4));

		List<UpgradeProcessInfo> upgradePath =
			releaseGraphManager.getUpgradePath("0", "200");

		Assert.assertEquals(Arrays.asList(up1, up2, up3, up4), upgradePath);
	}

}