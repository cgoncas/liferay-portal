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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Cristina González
 */
public class ClassData extends CoverageDataContainer
	implements Comparable<ClassData>, HasBeenInstrumented {

	public ClassData(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Class name must be specified.");
		}

		this.name = name;
	}

	public LineData addLine(
		int lineNumber, String methodName, String methodDescriptor) {

		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData == null) {
				lineData = new LineData(lineNumber);

				// Each key is a line number in this class, stored as an Integer
				// object. Each value is information about the line, stored as a
				// LineData object.

				children.put(new Integer(lineNumber), lineData);
			}

			lineData.setMethodNameAndDescriptor(methodName, methodDescriptor);

			// methodName and methodDescriptor can be null when cobertura.ser
			// with no line information was loaded (or was not loaded at all).

			if ( methodName!= null && methodDescriptor!= null) {
				methodNamesAndDescriptors.add(methodName + methodDescriptor);
			}

			return lineData;
		}
		finally {
			lock.unlock();
		}
	}

	public void addLineJump(int lineNumber, int branchNumber) {
		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData != null) {
				lineData.addJump(branchNumber);
				this.branches.put(Integer.valueOf(lineNumber), lineData);
			}
		}

		finally {
			lock.unlock();
		}
	}

	public void addLineSwitch(
		int lineNumber, int switchNumber, int min, int max) {

		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData != null) {
				lineData.addSwitch(switchNumber, min, max);

				this.branches.put(Integer.valueOf(lineNumber), lineData);
			}
		}
		finally {
			lock.unlock();
		}
	}

	public void addLineSwitch(int lineNumber, int switchNumber, int[] keys) {
		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData != null) {
				lineData.addSwitch(switchNumber, keys);
				this.branches.put(Integer.valueOf(lineNumber), lineData);
			}
		}
		finally {
			lock.unlock();
		}
	}

	public int compareTo(ClassData o) {
		return this.name.compareTo(o.name);
	}

	public boolean containsInstrumentationInfo() {
		lock.lock();

		try {
			return this.containsInstrumentationInfo;
		}
		finally {
			lock.unlock();
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || !obj.getClass().equals(this.getClass())) {
			return false;
		}

		ClassData classData = (ClassData)obj;

		getBothLocks(classData);

		try {
			return super.equals(obj) &&
				 this.branches.equals(classData.branches) &&
				 this.methodNamesAndDescriptors.equals(
					 classData.methodNamesAndDescriptors) &&
				 this.name.equals(classData.name) &&
				 this.sourceFileName.equals(classData.sourceFileName);
		}
		finally {
			lock.unlock();
			classData.lock.unlock();
		}
	}

	public String getBaseName() {
		int lastDot = this.name.lastIndexOf('.');

		if (lastDot == -1) {
			return this.name;
		}

		return this.name.substring(lastDot + 1);
	}

	public double getBranchCoverageRate(String methodNameAndDescriptor) {
		int total = 0;

		int covered = 0;

		lock.lock();

		try {
			for (Iterator<LineData> iter = branches.values().iterator();
				iter.hasNext();) {

				LineData next = (LineData)iter.next();

				String nextMethodNameAndDescriptor =
					next.getMethodName() + next.getMethodDescriptor();

				if (methodNameAndDescriptor.equals(
						nextMethodNameAndDescriptor)) {

					total += next.getNumberOfValidBranches();

					covered += next.getNumberOfCoveredBranches();
				}
			}

			if (total == 0) {
				return 1.0;
			}

			return (double)covered / total;
		}

		finally {
			lock.unlock();
		}
	}

	public Collection<Integer> getBranches() {
		lock.lock();

		try {
			return Collections.unmodifiableCollection(branches.keySet());
		}
		finally {
			lock.unlock();
		}
	}

	public LineData getLineCoverage(int lineNumber) {
		Integer lineObject = new Integer(lineNumber);

		lock.lock();

		try {
			if (!children.containsKey(lineObject)) {
				return null;
			}

			return (LineData)children.get(lineObject);
		}
		finally {
			lock.unlock();
		}
	}

	public double getLineCoverageRate(String methodNameAndDescriptor) {
		int total = 0;

		int hits = 0;

		lock.lock();

		try {
			Iterator<CoverageData> iter = children.values().iterator();

			while (iter.hasNext()) {
				LineData next = (LineData)iter.next();

				if (methodNameAndDescriptor.equals(
						next.getMethodName() + next.getMethodDescriptor())) {

					total++;

					if (next.getHits() > 0) {
						hits++;
					}
				}
			}

			if (total == 0) {
				return 1d;
			}

			return (double)hits / total;
		}
		finally {
			lock.unlock();
		}
	}

	public SortedSet<CoverageData> getLines() {
		lock.lock();

		try {
			return new TreeSet<>(this.children.values());
		}
		finally {
			lock.unlock();
		}
	}

	public Collection<CoverageData> getLines(String methodNameAndDescriptor) {
		Collection<CoverageData> lines = new HashSet<>();

		lock.lock();

		try {
			Iterator<CoverageData> iter = children.values().iterator();

			while (iter.hasNext()) {
				LineData next = (LineData)iter.next();

				String nextMethodNameAndDescriptor =
					next.getMethodName() + next.getMethodDescriptor();

				if (methodNameAndDescriptor.equals(
						nextMethodNameAndDescriptor)) {

					lines.add(next);
				}
			}

			return lines;
		}
		finally {
			lock.unlock();
		}
	}

	public Set<String> getMethodNamesAndDescriptors() {
		lock.lock();

		try {
			return methodNamesAndDescriptors;
		}

		finally {
			lock.unlock();
		}
	}

	public String getName() {
		return name;
	}

	public int getNumberOfCoveredBranches() {
		int number = 0;

		lock.lock();

		try {
			for (Iterator<LineData> i = branches.values().iterator();
				i.hasNext(); number += (i.next()).getNumberOfCoveredBranches());

			return number;
		}
		finally {
			lock.unlock();
		}
	}

	public int getNumberOfValidBranches() {
		int number = 0;

		lock.lock();

		try {
			for (Iterator<LineData> i = branches.values().iterator();
				i.hasNext();
				number += (i.next()).getNumberOfValidBranches());

			return number;
		}

		finally {
			lock.unlock();
		}
	}

	public String getPackageName() {
		int lastDot = this.name.lastIndexOf('.');

		if (lastDot == -1) {
			return "";
		}

		return this.name.substring(0, lastDot);
	}

	public String getSourceFileName() {
		String baseName;

		lock.lock();

		try {
			if (sourceFileName != null) {
				baseName = sourceFileName;
			}
			else {
				baseName = getBaseName();

				int firstDollarSign = baseName.indexOf('$');

				if ((firstDollarSign == -1) || (firstDollarSign == 0)) {
					baseName += ".java";
				}
				else {
					baseName = baseName.substring(0, firstDollarSign) + ".java";
				}
			}

			String packageName = getPackageName();

			if (packageName.equals("")) {
				return baseName;
			}

			return packageName.replace('.', '/') + '/' + baseName;
		}

		finally {
			lock.unlock();
		}
	}

	public boolean hasBranch(int lineNumber) {
		lock.lock();

		try {
			return branches.containsKey(Integer.valueOf(lineNumber));
		}
		finally {
			lock.unlock();
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public boolean isValidSourceLineNumber(int lineNumber) {
		lock.lock();

		try {
			return children.containsKey(Integer.valueOf(lineNumber));
		}
		finally {
			lock.unlock();
		}
	}

	public void merge(CoverageData coverageData) {
		ClassData classData = (ClassData)coverageData;

		// If objects contain data for different classes then don't merge

		if (!this.getName().equals(classData.getName())) {
			return;
		}

		getBothLocks(classData);

		try {
			super.merge(coverageData);

			// We can't just call this.branches.putAll(classData.branches);
			// Why not?  If we did a putAll, then the LineData objects from
			// the coverageData class would overwrite the LineData objects
			// that are already in "this.branches"  And we don't need to
			// update the LineData objects that are already in this.branches
			// because they are shared between this.branches and this.children,
			// so the object hit counts will be moved when we called
			// super.merge() above.

			for (Iterator<Integer> iter =
					 classData.branches.keySet().iterator(); iter.hasNext();) {

				Integer key = iter.next();

				if (!this.branches.containsKey(key)) {
					this.branches.put(key, classData.branches.get(key));
				}
			}

			this.containsInstrumentationInfo |=
				classData.containsInstrumentationInfo;

			this.methodNamesAndDescriptors.addAll(
				classData.getMethodNamesAndDescriptors());

			if (classData.sourceFileName != null) {
				this.sourceFileName = classData.sourceFileName;
			}
		}
		finally {
			lock.unlock();
			classData.lock.unlock();
		}
	}

	public void removeLine(int lineNumber) {
		Integer lineObject = Integer.valueOf(lineNumber);

		lock.lock();

		try {
			children.remove(lineObject);
			branches.remove(lineObject);
		}
		finally {
			lock.unlock();
		}
	}

	public void setBranches(Map<Integer, LineData> branches) {
		this.branches = branches;
	}

	public void setContainsInstrumentationInfo() {
		lock.lock();

		try {
			this.containsInstrumentationInfo = true;
		}

		finally
		{
			lock.unlock();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSourceFileName(String sourceFileName) {
		lock.lock();

		try {
			this.sourceFileName = sourceFileName;
		}
		finally {
			lock.unlock();
		}
	}

	public void touch(int lineNumber, int hits) {
		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData == null) {
				lineData = addLine(lineNumber, null, null);
			}

			lineData.touch(hits);
		}
		finally {
			lock.unlock();
		}
	}

	public void touchJump(
		int lineNumber, int branchNumber, boolean branch, int hits) {

		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData == null) {
				lineData = addLine(lineNumber, null, null);
			}

			lineData.touchJump(branchNumber, branch, hits);
		}
		finally {
			lock.unlock();
		}
	}

	public void touchSwitch(
		int lineNumber, int switchNumber, int branch, int hits) {

		lock.lock();

		try {
			LineData lineData = getLineData(lineNumber);

			if (lineData == null) {
				lineData = addLine(lineNumber, null, null);
			}

			lineData.touchSwitch(switchNumber, branch, hits);
		}
		finally {
			lock.unlock();
		}
	}

	private LineData getLineData(int lineNumber) {
		lock.lock();

		try {
			return (LineData)children.get(Integer.valueOf(lineNumber));
		}
		finally {
			lock.unlock();
		}
	}

	private static final long serialVersionUID = 5;

	/**
	 * Each key is a line number in this class, stored as an Integer object.
	 * Each value is information about the line, stored as a LineData object.
	 */
	private Map<Integer, LineData> branches = new HashMap<>();

	private boolean containsInstrumentationInfo = false;
	private final Set<String> methodNamesAndDescriptors = new HashSet<>();
	private String name = null;
	private String sourceFileName = null;

}