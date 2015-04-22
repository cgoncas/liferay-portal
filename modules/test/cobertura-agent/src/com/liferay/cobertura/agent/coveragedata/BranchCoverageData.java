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

package com.liferay.cobertura.agent.coveragedata;

/**
 * @author Cristina González
 */
public interface BranchCoverageData {

	public double getBranchCoverageRate();

	public int getNumberOfCoveredBranches();

	public int getNumberOfValidBranches();

	/**
	 * Warning: This is generally implemented as a
	 * "shallow" merge.  For our current use, this
	 * should be fine, but in the future it may make
	 * sense to modify the merge methods of the
	 * various classes to do a deep copy of the
	 * appropriate objects.
	 */
	public void merge(BranchCoverageData coverageData);

}