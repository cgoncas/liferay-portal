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

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.v7_0_0.util.ReleaseTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Carlos Sierra Andrés
 */
public class UpgradeRelease extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			runSQL("alter_column_type Release_ buildNumber VARCHAR(255) null");
		}
		catch (SQLException sqle) {
			upgradeTable(
				ReleaseTable.TABLE_NAME,
				ReleaseTable.TABLE_COLUMNS,
				ReleaseTable.TABLE_SQL_CREATE,
				ReleaseTable.TABLE_SQL_ADD_INDEXES);
		}

		Connection con = DataAccess.getUpgradeOptimizedConnection();

		try (
			PreparedStatement ps1 = con.prepareStatement(
				"select releaseId, buildNumber from Release_");
			ResultSet rs = ps1.executeQuery()) {

			while (rs.next()) {
				long releaseId = rs.getLong("releaseId");
				String buildNumber = rs.getString("buildNumber");

				String version = transformVersion(buildNumber);

				try (PreparedStatement ps2 = con.prepareStatement(
					"update Release_ set buildNumber=? where releaseId=?")) {

					ps2.setString(1, version);
					ps2.setLong(2, releaseId);

					ps2.executeUpdate();
				}
			}
		}
		finally {
			DataAccess.cleanUp(con);
		}
	}

	private String transformVersion(String buildNumber) {
		String major = buildNumber.substring(0, 1);
		String minor = buildNumber.substring(1, 2);
		String micro = buildNumber.substring(2);

		return major + "." + minor + "." + micro;
	}

}
