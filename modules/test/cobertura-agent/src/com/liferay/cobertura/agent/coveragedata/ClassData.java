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

		_name = name;
	}

	public LineData addLine(
		int lineNumber, String methodName, String methodDescriptor) {

		lock.lock();

		try {
			LineData lineData = _getLineData(lineNumber);

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
				_methodNamesAndDescriptors.add(methodName + methodDescriptor);
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
			LineData lineData = _getLineData(lineNumber);

			if (lineData != null) {
				lineData.addJump(branchNumber);
				_branches.put(Integer.valueOf(lineNumber), lineData);
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
			LineData lineData = _getLineData(lineNumber);

			if (lineData != null) {
				lineData.addSwitch(switchNumber, min, max);

				_branches.put(Integer.valueOf(lineNumber), lineData);
			}
		}
		finally {
			lock.unlock();
		}
	}

	public void addLineSwitch(int lineNumber, int switchNumber, int[] keys) {
		lock.lock();

		try {
			LineData lineData = _getLineData(lineNumber);

			if (lineData != null) {
				lineData.addSwitch(switchNumber, keys);
				_branches.put(Integer.valueOf(lineNumber), lineData);
			}
		}
		finally {
			lock.unlock();
		}
	}

	public int compareTo(ClassData o) {
		return _name.compareTo(o._name);
	}

	public boolean containsInstrumentationInfo() {
		lock.lock();

		try {
			return _containsInstrumentationInfo;
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
				 _branches.equals(classData._branches) &&
				 _methodNamesAndDescriptors.equals(
					 classData._methodNamesAndDescriptors) &&
				 _name.equals(classData._name) &&
				 _sourceFileName.equals(classData._sourceFileName);
		}
		finally {
			lock.unlock();
			classData.lock.unlock();
		}
	}

	public String getBaseName() {
		int lastDot = _name.lastIndexOf('.');

		if (lastDot == -1) {
			return _name;
		}

		return _name.substring(lastDot + 1);
	}

	public double getBranchCoverageRate(String methodNameAndDescriptor) {
		int total = 0;

		int covered = 0;

		lock.lock();

		try {
			for (Iterator<LineData> iter = _branches.values().iterator();
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
			return Collections.unmodifiableCollection(_branches.keySet());
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
			return _methodNamesAndDescriptors;
		}

		finally {
			lock.unlock();
		}
	}

	public String getName() {
		return _name;
	}

	public int getNumberOfCoveredBranches() {
		int number = 0;

		lock.lock();

		try {
			for (Iterator<LineData> i = _branches.values().iterator();
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
			for (Iterator<LineData> i = _branches.values().iterator();
				i.hasNext();
				number += (i.next()).getNumberOfValidBranches());

			return number;
		}

		finally {
			lock.unlock();
		}
	}

	public String getPackageName() {
		int lastDot = _name.lastIndexOf('.');

		if (lastDot == -1) {
			return "";
		}

		return _name.substring(0, lastDot);
	}

	public String getSourceFileName() {
		String baseName;

		lock.lock();

		try {
			if (_sourceFileName != null) {
				baseName = _sourceFileName;
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
			return _branches.containsKey(Integer.valueOf(lineNumber));
		}
		finally {
			lock.unlock();
		}
	}

	public int hashCode() {
		return _name.hashCode();
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

			// We can't just call _branches.putAll(classData._branches);
			// Why not?  If we did a putAll, then the LineData objects from
			// the coverageData class would overwrite the LineData objects
			// that are already in "_branches"  And we don't need to
			// update the LineData objects that are already in this._branches
			// because they are shared between _branches and this.children,
			// so the object hit counts will be moved when we called
			// super.merge() above.

			for (Iterator<Integer> iter =
					 classData._branches.keySet().iterator(); iter.hasNext();) {

				Integer key = iter.next();

				if (!_branches.containsKey(key)) {
					_branches.put(key, classData._branches.get(key));
				}
			}

			_containsInstrumentationInfo |=
				classData._containsInstrumentationInfo;

			_methodNamesAndDescriptors.addAll(
				classData.getMethodNamesAndDescriptors());

			if (classData._sourceFileName != null) {
				_sourceFileName = classData._sourceFileName;
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
			_branches.remove(lineObject);
		}
		finally {
			lock.unlock();
		}
	}

	public void setBranches(Map<Integer, LineData> branches) {
		_branches = branches;
	}

	public void setContainsInstrumentationInfo() {
		lock.lock();

		try {
			_containsInstrumentationInfo = true;
		}

		finally
		{
			lock.unlock();
		}
	}

	public void setName(String name) {
		_name = name;
	}

	public void setSourceFileName(String sourceFileName) {
		lock.lock();

		try {
			_sourceFileName = sourceFileName;
		}
		finally {
			lock.unlock();
		}
	}

	public void touch(int lineNumber, int hits) {
		lock.lock();

		try {
			LineData lineData = _getLineData(lineNumber);

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
			LineData lineData = _getLineData(lineNumber);

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
			LineData lineData = _getLineData(lineNumber);

			if (lineData == null) {
				lineData = addLine(lineNumber, null, null);
			}

			lineData.touchSwitch(switchNumber, branch, hits);
		}
		finally {
			lock.unlock();
		}
	}

	private LineData _getLineData(int lineNumber) {
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
	private Map<Integer, LineData> _branches = new HashMap<>();
	
	private boolean _containsInstrumentationInfo = false;
	private Set<String> _methodNamesAndDescriptors = new HashSet<>();
	private String _name = null;
	private String _sourceFileName = null;

}