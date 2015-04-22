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
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Cristina González
 */
public class PackageData extends CoverageDataContainer
		implements Comparable, HasBeenInstrumented {

	public PackageData(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
				"Package name must be specified.");
		}

		this.name = name;
	}

	public void addClassData(ClassData classData) {
		lock.lock();

		try {
			if (children.containsKey(classData.getBaseName())) {
				throw new IllegalArgumentException(
					"Package " + this.name +
						" already contains a class with the name " +
						classData.getBaseName());
			}

			// Each key is a class basename, stored as an String object.
			// Each value is information about the class, stored as a ClassData
			// object.

			children.put(classData.getBaseName(), classData);
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * This is required because we implement Comparable.
	 */
	public int compareTo(Object o) {
		if (!o.getClass().equals(PackageData.class))return Integer.MAX_VALUE;
		return this.name.compareTo(((PackageData)o).name);
	}

	public boolean contains(String name) {
		lock.lock();

		try {
			return this.children.containsKey(name);
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * Returns true if the given object is an instance of the
	 * PackageData class, and it contains the same data as this
	 * class.
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || !obj.getClass().equals(this.getClass())) {
			return false;
		}

		PackageData packageData = (PackageData)obj;
		getBothLocks(packageData);

		try {
			return super.equals(obj) && this.name.equals(packageData.name);
		}
		finally {
			lock.unlock();
			packageData.lock.unlock();
		}
	}

	public SortedSet getClasses() {
		lock.lock();

		try {
			return new TreeSet(this.children.values());
		}
		finally {
			lock.unlock();
		}
	}

	public String getName() {
		return this.name;
	}

	public String getSourceFileName() {
		return this.name.replace('.', '/');
	}

	public Collection getSourceFiles() {
		SortedMap sourceFileDatas = new TreeMap();

		lock.lock();

		try {
			Iterator iter = this.children.values().iterator();

			while (iter.hasNext()) {
				ClassData classData = (ClassData)iter.next();
				String sourceFileName = classData.getSourceFileName();
				SourceFileData sourceFileData =
					(SourceFileData)sourceFileDatas.get(sourceFileName);

				if (sourceFileData == null) {
					sourceFileData = new SourceFileData(sourceFileName);
					sourceFileDatas.put(sourceFileName, sourceFileData);
				}

				sourceFileData.addClassData(classData);
			}
		}
		finally {
			lock.unlock();
		}

		return sourceFileDatas.values();
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public void setName(String name) {
		this.name = name;
	}

	private static final long serialVersionUID = 7;

	private String name;

}