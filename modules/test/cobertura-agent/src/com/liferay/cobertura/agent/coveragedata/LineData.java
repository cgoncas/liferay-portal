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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sourceforge.cobertura.util.StringUtil;

/**
 * @author Cristina González
 */
public class LineData
		implements Comparable, CoverageData, HasBeenInstrumented, Serializable {

	public LineData(int lineNumber) {
		this(lineNumber, null, null);
	}

	public LineData(
		int lineNumber, String methodName, String methodDescriptor) {

		this.hits = 0;
		this.jumps = null;
		this.lineNumber = lineNumber;
		this.methodName = methodName;
		this.methodDescriptor = methodDescriptor;
		initLock();
	}

	public int compareTo(Object o) {
		if (!o.getClass().equals(LineData.class)) {
			return Integer.MAX_VALUE;
		}

		return this.lineNumber - ((LineData)o).lineNumber;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || !obj.getClass().equals(this.getClass())) {
			return false;
		}

		LineData lineData = (LineData)obj;
		getBothLocks(lineData);

		try {
			return (this.hits == lineData.hits) &&
					((this.jumps == lineData.jumps) ||
					((this.jumps != null) &&
					(this.jumps.equals(lineData.jumps)))) &&
					((this.switches == lineData.switches) ||
					((this.switches != null) &&
					(this.switches.equals(lineData.switches)))) &&
					(this.lineNumber == lineData.lineNumber) &&
					(this.methodDescriptor.equals(lineData.methodDescriptor)) &&
					(this.methodName.equals(lineData.methodName));
		}
		finally {
			lock.unlock();
			lineData.lock.unlock();
		}
	}

	public double getBranchCoverageRate() {
		if (getNumberOfValidBranches() == 0) {
			return 1d;
		}

		lock.lock();

		try {
			return ((double)getNumberOfCoveredBranches()) /
				getNumberOfValidBranches();
		}
		finally {
			lock.unlock();
		}
	}

	public String getConditionCoverage() {
		StringBuffer ret = new StringBuffer();

		if (getNumberOfValidBranches() == 0) {
			ret.append(StringUtil.getPercentValue(1.0));
		}
		else {
			lock.lock();

			try {
				ret.append(StringUtil.getPercentValue(getBranchCoverageRate()));

				ret.append(
					" (").append(getNumberOfCoveredBranches()).append("/").
					append(getNumberOfValidBranches()).append(")");
			}
			finally {
				lock.unlock();
			}
		}

		return ret.toString();
	}

	public String getConditionCoverage(int index) {
		Object branchData = getConditionData(index);

		if (branchData == null) {
			return StringUtil.getPercentValue(1.0);
		}
		else if (branchData instanceof JumpData) {
			JumpData jumpData = (JumpData)branchData;
			return StringUtil.getPercentValue(jumpData.getBranchCoverageRate());
		}

		else {
			SwitchData switchData = (SwitchData)branchData;
			return StringUtil.getPercentValue(
				switchData.getBranchCoverageRate());
		}
	}

	public Object getConditionData(int index) {
		Object branchData = null;

		lock.lock();

		try {
			int jumpsSize = (jumps == null) ? 0 : jumps.size();
			int switchesSize = (switches == null) ? 0 :switches.size();

			if (index < jumpsSize) {
				branchData = jumps.get(index);
			}
			else if (index < (jumpsSize + switchesSize)) {
				branchData = switches.get(index - jumpsSize);
			}

			return branchData;
		}
		finally {
			lock.unlock();
		}
	}

	public int getConditionSize() {
		lock.lock();

		try {
			int jumpsSize = 0;

			int switchsSize = 0;

			if (jumps != null)jumpsSize = jumps.size();

			if (switches != null)switchsSize = switches.size();

			return jumpsSize + switchsSize;
		}
		finally {
			lock.unlock();
		}
	}

	public long getHits() {
		lock.lock();

		try {
			return hits;
		}

		finally {
			lock.unlock();
		}
	}

	public double getLineCoverageRate() {
		return (getHits() > 0) ? 1 : 0;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getMethodDescriptor() {
		lock.lock();

		try {
			return methodDescriptor;
		}
		finally {
			lock.unlock();
		}
	}

	public String getMethodName() {
		lock.lock();

		try {
			return methodName;
		}
		finally {
			lock.unlock();
		}
	}

	public int getNumberOfCoveredBranches() {
		int ret = 0;
		lock.lock();

		try {
			if (jumps != null) {
				for (int i = jumps.size() - 1; i >= 0; i--) {
					ret +=
						((JumpData)jumps.get(i)).getNumberOfCoveredBranches();
				}
			}

			if (switches != null) {
				for (int i = switches.size() - 1; i >= 0; i--) {
					ret +=
						((SwitchData)switches.get(i)).
							getNumberOfCoveredBranches();
				}
			}

			return ret;
		}
		finally {
			lock.unlock();
		}
	}

	public int getNumberOfCoveredLines() {
		return (getHits() > 0) ? 1 : 0;
	}

	public int getNumberOfValidBranches() {
		int ret = 0;

		lock.lock();

		try {
			if (jumps != null) {
				for (int i = jumps.size() - 1; i >= 0; i--) {
					ret += ((JumpData)jumps.get(i)).getNumberOfValidBranches();
				}
			}

			if (switches != null) {
				for (int i = switches.size() - 1; i >= 0; i--)
					ret +=
						((SwitchData)switches.get(i)).
							getNumberOfValidBranches();
			}

			return ret;
		}
		finally {
			lock.unlock();
		}
	}

	public int getNumberOfValidLines() {
		return 1;
	}

	public boolean hasBranch() {
		lock.lock();

		try {
			return (jumps != null) || (switches != null);
		}
		finally {
			lock.unlock();
		}
	}

	public int hashCode() {
		return this.lineNumber;
	}

	public boolean isCovered() {
		lock.lock();

		try {
			return (getHits() > 0) && ((getNumberOfValidBranches() == 0) ||
			 ((1.0 - getBranchCoverageRate()) < 0.0001));
		}
		finally {
			lock.unlock();
		}
	}

	public void merge(CoverageData coverageData) {
		LineData lineData = (LineData)coverageData;
		getBothLocks(lineData);

		try {
			this.hits += lineData.hits;

			if (lineData.jumps != null) {
				if (this.jumps == null) {
					this.jumps = lineData.jumps;
				}
				else {
					for (int i = Math.min(
						this.jumps.size(), lineData.jumps.size()) - 1; i >= 0;
						i--) {

						((JumpData)this.jumps.get(i)).merge(
							(JumpData)lineData.jumps.get(i));
					}

					for (int i = Math.min(
						this.jumps.size(), lineData.jumps.size());
						i < lineData.jumps.size(); i++) {

						this.jumps.add(lineData.jumps.get(i));
					}
				}
			}

			if (lineData.switches != null) {
				if (this.switches == null) {
					this.switches = lineData.switches;
				}
				else {
					for (int i = Math.min(
						this.switches.size(), lineData.switches.size()) - 1;
						i >= 0; i--) {

						((SwitchData)this.switches.get(i)).merge(
							(SwitchData)lineData.switches.get(i));
					}

					for (int i = Math.min(
						this.switches.size(), lineData.switches.size());
						i < lineData.switches.size(); i++) {

						this.switches.add(lineData.switches.get(i));
					}
				}
			}

			if (lineData.methodName != null)
				this.methodName = lineData.methodName;

			if (lineData.methodDescriptor != null)
				this.methodDescriptor = lineData.methodDescriptor;
		}
		finally {
			lock.unlock();
			lineData.lock.unlock();
		}
	}

	public void touch(int new_hits) {
		lock.lock();

		try {
			this.hits+= new_hits;
		}

		finally {
			lock.unlock();
		}
	}

	public void touchJump(int jumpNumber, boolean branch, int hits) {
		getJumpData(jumpNumber).touchBranch(branch, hits);
	}

	public void touchSwitch(int switchNumber, int branch, int hits) {
		getSwitchData(switchNumber, null).touchBranch(branch, hits);
	}

	protected void addJump(int jumpNumber) {
		getJumpData(jumpNumber);
	}

	protected void addSwitch(int switchNumber, int min, int max) {
		getSwitchData(switchNumber, new SwitchData(switchNumber, min, max));
	}

	protected void addSwitch(int switchNumber, int[] keys) {
		getSwitchData(switchNumber, new SwitchData(switchNumber, keys));
	}

	protected JumpData getJumpData(int jumpNumber) {
		lock.lock();

		try {
			if (jumps == null) {
				jumps = new ArrayList();
			}

			if (jumps.size() <= jumpNumber) {
				for (int i = jumps.size(); i <= jumpNumber;
					jumps.add(new JumpData(i++)));
			}

			return (JumpData)jumps.get(jumpNumber);
		}
		finally {
			lock.unlock();
		}
	}

	protected SwitchData getSwitchData(int switchNumber, SwitchData data) {
		lock.lock();

		try {
			if (switches == null) {
				switches = new ArrayList();
			}

			if (switches.size() < switchNumber) {
				for (int i = switches.size(); i < switchNumber;
					 switches.add(new SwitchData(i++)));
			}

			if (switches.size() == switchNumber) {
				if (data != null) {
					switches.add(data);
				}
				else {
					switches.add(new SwitchData(switchNumber));
				}
			}

			return (SwitchData)switches.get(switchNumber);
		}
		finally {
			lock.unlock();
		}
	}

	protected void setMethodNameAndDescriptor(String name, String descriptor) {
		lock.lock();

		try {
			this.methodName = name;
			this.methodDescriptor = descriptor;
		}
		finally {
			lock.unlock();
		}
	}

	private void getBothLocks(LineData other) {
		/*
		 * To prevent deadlock, we need to get both locks or none at all.
		 *
		 * When this method returns, the thread will have both locks.
		 * Make sure you unlock them!
		 */
		boolean myLock = false;

		boolean otherLock = false;

		while (!myLock || !otherLock) {
			try {
				myLock = lock.tryLock();
				otherLock = other.lock.tryLock();
			}
			finally {
				if ((!myLock) || (!otherLock))
				{
					//could not obtain both locks - so unlock the one we got.

					if (myLock) {
						lock.unlock();
					}

					if (otherLock) {
						other.lock.unlock();
					}

					//do a yield so the other threads will get to work.

					Thread.yield();
				}
			}
		}
	}

	private void initLock() {
		lock = new ReentrantLock();
	}

	private void readObject(ObjectInputStream in)
		throws ClassNotFoundException, IOException {

		in.defaultReadObject();
		initLock();
	}

	private static final long serialVersionUID = 4;

	private long hits;
	private List jumps;
	private int lineNumber;
	private transient Lock lock;
	private String methodDescriptor;
	private String methodName;
	private List switches;

}