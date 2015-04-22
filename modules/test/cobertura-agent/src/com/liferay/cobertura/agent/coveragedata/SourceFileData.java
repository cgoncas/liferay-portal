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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.cobertura.util.StringUtil;

/**
 * @author Cristina González
 */
public class SourceFileData extends CoverageDataContainer
	implements Comparable, HasBeenInstrumented {

	public SourceFileData(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
				"Source file name must be specified.");
		}

		_name = name;
	}

	public void addClassData(ClassData classData) {
		lock.lock();

		try {
			if (children.containsKey(classData.getBaseName())) {
				throw new IllegalArgumentException(
					"Source file " + _name + " already contains a class " +
						"with the name " + classData.getBaseName());
			}

			// Each key is a class basename, stored as an String object.
			// Each value is information about the class, stored as a
			// ClassData object.

			children.put(classData.getBaseName(), classData);
		}
		finally {
			lock.unlock();
		}
	}

	public int compareTo(Object o) {
		if (!o.getClass().equals(SourceFileData.class))
			return Integer.MAX_VALUE;
		return _name.compareTo(((SourceFileData)o)._name);
	}

	public boolean contains(String name) {
		lock.lock();

		try {
			return children.containsKey(name);
		}
		finally {
			lock.unlock();
		}
	}

	public boolean containsInstrumentationInfo() {
		lock.lock();

		try {

			// Return false if any of our child ClassData's does not
			// contain instrumentation info

			Iterator iter = children.values().iterator();
			while (iter.hasNext()) {
				ClassData classData = (ClassData)iter.next();

				if (!classData.containsInstrumentationInfo()) {
					return false;
				}
			}
		}
		finally {
			lock.unlock();
		}

		return true;
	}

	/**
	 * Returns true if the given object is an instance of the
	 * SourceFileData class, and it contains the same data as this
	 * class.
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || !obj.getClass().equals(getClass())) {
			return false;
		}

		SourceFileData sourceFileData = (SourceFileData)obj;
		getBothLocks(sourceFileData);

		try {
			return super.equals(obj) && _name.equals(sourceFileData._name);
		}
		finally {
			lock.unlock();
			sourceFileData.lock.unlock();
		}
	}

	public String getBaseName() {
		String fullNameWithoutExtension;
		int lastDot = _name.lastIndexOf('.');

		if (lastDot == -1) {
			fullNameWithoutExtension = _name;
		}
		else {
			fullNameWithoutExtension = _name.substring(0, lastDot);
		}

		int lastSlash = fullNameWithoutExtension.lastIndexOf('/');

		if (lastSlash == -1) {
			return fullNameWithoutExtension;
		}

		return fullNameWithoutExtension.substring(lastSlash + 1);
	}

	public SortedSet getClasses() {
		lock.lock();

		try {
			return new TreeSet(children.values());
		}
		finally {
			lock.unlock();
		}
	}

	public LineData getLineCoverage(int lineNumber) {
		lock.lock();

		try {
			Iterator iter = children.values().iterator();
			while (iter.hasNext()) {
				ClassData classData = (ClassData)iter.next();

				if (classData.isValidSourceLineNumber(lineNumber))
					return classData.getLineCoverage(lineNumber);
			}
		}
		finally {
			lock.unlock();
		}

		return null;
	}

	public String getName() {
		return _name;
	}

	public String getNormalizedName() {
		String fullNameWithoutExtension;
		int lastDot = _name.lastIndexOf('.');

		if (lastDot == -1) {
			fullNameWithoutExtension = _name;
		}

		else {
			fullNameWithoutExtension = _name.substring(0, lastDot);
		}

		return StringUtil.replaceAll(fullNameWithoutExtension, "/", ".");
	}

	public String getPackageName() {
		int lastSlash = _name.lastIndexOf('/');

		if (lastSlash == -1) {
			return null;
		}

		return StringUtil.replaceAll(
			_name.substring(0, lastSlash), "/", ".");
	}

	public int hashCode() {
		return _name.hashCode();
	}

	public boolean isValidSourceLineNumber(int lineNumber) {
		lock.lock();

		try {
			Iterator iter = children.values().iterator();

			while (iter.hasNext()) {
				ClassData classData = (ClassData)iter.next();

				if (classData.isValidSourceLineNumber(lineNumber))return true;
			}
		}
		finally {
			lock.unlock();
		}

		return false;
	}

	public void setName(String name) {
		_name = name;
	}

	private static final long serialVersionUID = 3;

	private String _name;

}