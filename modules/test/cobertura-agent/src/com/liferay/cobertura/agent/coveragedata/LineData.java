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

		_hits = 0;
		_jumps = null;
		_lineNumber = lineNumber;
		_methodName = methodName;
		_methodDescriptor = methodDescriptor;
		initLock();
	}

	public int compareTo(Object o) {
		if (!o.getClass().equals(LineData.class)) {
			return Integer.MAX_VALUE;
		}

		return _lineNumber - ((LineData)o)._lineNumber;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || !obj.getClass().equals(getClass())) {
			return false;
		}

		LineData lineData = (LineData)obj;
		getBothLocks(lineData);

		try {
			return (_hits == lineData._hits) &&
					((_jumps == lineData._jumps) ||
					((_jumps != null) &&
					(_jumps.equals(lineData._jumps)))) &&
					((_switches == lineData._switches) ||
					((_switches != null) &&
					(_switches.equals(lineData._switches)))) &&
					(_lineNumber == lineData._lineNumber) &&
					(_methodDescriptor.equals(lineData._methodDescriptor)) &&
					(_methodName.equals(lineData._methodName));
		}
		finally {
			_lock.unlock();
			lineData._lock.unlock();
		}
	}

	public double getBranchCoverageRate() {
		if (getNumberOfValidBranches() == 0) {
			return 1d;
		}

		_lock.lock();

		try {
			return ((double)getNumberOfCoveredBranches()) /
				getNumberOfValidBranches();
		}
		finally {
			_lock.unlock();
		}
	}

	public String getConditionCoverage() {
		StringBuffer ret = new StringBuffer();

		if (getNumberOfValidBranches() == 0) {
			ret.append(StringUtil.getPercentValue(1.0));
		}
		else {
			_lock.lock();

			try {
				ret.append(StringUtil.getPercentValue(getBranchCoverageRate()));

				ret.append(
					" (").append(getNumberOfCoveredBranches()).append("/").
					append(getNumberOfValidBranches()).append(")");
			}
			finally {
				_lock.unlock();
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

		_lock.lock();

		try {
			int jumpsSize = (_jumps == null) ? 0 : _jumps.size();
			int switchesSize = (_switches == null) ? 0 : _switches.size();

			if (index < jumpsSize) {
				branchData = _jumps.get(index);
			}
			else if (index < (jumpsSize + switchesSize)) {
				branchData = _switches.get(index - jumpsSize);
			}

			return branchData;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getConditionSize() {
		_lock.lock();

		try {
			int jumpsSize = 0;

			int switchsSize = 0;

			if (_jumps != null)jumpsSize = _jumps.size();

			if (_switches != null)switchsSize = _switches.size();

			return jumpsSize + switchsSize;
		}
		finally {
			_lock.unlock();
		}
	}

	public long getHits() {
		_lock.lock();

		try {
			return _hits;
		}

		finally {
			_lock.unlock();
		}
	}

	public double getLineCoverageRate() {
		return (getHits() > 0) ? 1 : 0;
	}

	public int getLineNumber() {
		return _lineNumber;
	}

	public String getMethodDescriptor() {
		_lock.lock();

		try {
			return _methodDescriptor;
		}
		finally {
			_lock.unlock();
		}
	}

	public String getMethodName() {
		_lock.lock();

		try {
			return _methodName;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getNumberOfCoveredBranches() {
		int ret = 0;
		_lock.lock();

		try {
			if (_jumps != null) {
				for (int i = _jumps.size() - 1; i >= 0; i--) {
					ret +=
						((JumpData) _jumps.get(i)).getNumberOfCoveredBranches();
				}
			}

			if (_switches != null) {
				for (int i = _switches.size() - 1; i >= 0; i--) {
					ret +=
						((SwitchData) _switches.get(i)).
							getNumberOfCoveredBranches();
				}
			}

			return ret;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getNumberOfCoveredLines() {
		return (getHits() > 0) ? 1 : 0;
	}

	public int getNumberOfValidBranches() {
		int ret = 0;

		_lock.lock();

		try {
			if (_jumps != null) {
				for (int i = _jumps.size() - 1; i >= 0; i--) {
					ret += ((JumpData) _jumps.get(i)).getNumberOfValidBranches();
				}
			}

			if (_switches != null) {
				for (int i = _switches.size() - 1; i >= 0; i--)
					ret +=
						((SwitchData) _switches.get(i)).
							getNumberOfValidBranches();
			}

			return ret;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getNumberOfValidLines() {
		return 1;
	}

	public boolean hasBranch() {
		_lock.lock();

		try {
			return (_jumps != null) || (_switches != null);
		}
		finally {
			_lock.unlock();
		}
	}

	public int hashCode() {
		return _lineNumber;
	}

	public boolean isCovered() {
		_lock.lock();

		try {
			return (getHits() > 0) && ((getNumberOfValidBranches() == 0) ||
			 ((1.0 - getBranchCoverageRate()) < 0.0001));
		}
		finally {
			_lock.unlock();
		}
	}

	public void merge(CoverageData coverageData) {
		LineData lineData = (LineData)coverageData;
		getBothLocks(lineData);

		try {
			_hits += lineData._hits;

			if (lineData._jumps != null) {
				if (_jumps == null) {
					_jumps = lineData._jumps;
				}
				else {
					for (int i = Math.min(
						_jumps.size(), lineData._jumps.size()) - 1; i >= 0;
						i--) {

						((JumpData)_jumps.get(i)).merge(
							(JumpData)lineData._jumps.get(i));
					}

					for (int i = Math.min(
						_jumps.size(), lineData._jumps.size());
						i < lineData._jumps.size(); i++) {

						_jumps.add(lineData._jumps.get(i));
					}
				}
			}

			if (lineData._switches != null) {
				if (_switches == null) {
					_switches = lineData._switches;
				}
				else {
					for (int i = Math.min(
						_switches.size(), lineData._switches.size()) - 1;
						i >= 0; i--) {

						((SwitchData)_switches.get(i)).merge(
							(SwitchData)lineData._switches.get(i));
					}

					for (int i = Math.min(
						_switches.size(), lineData._switches.size());
						i < lineData._switches.size(); i++) {

						_switches.add(lineData._switches.get(i));
					}
				}
			}

			if (lineData._methodName != null)
				_methodName = lineData._methodName;

			if (lineData._methodDescriptor != null)
				_methodDescriptor = lineData._methodDescriptor;
		}
		finally {
			_lock.unlock();
			lineData._lock.unlock();
		}
	}

	public void touch(int new_hits) {
		_lock.lock();

		try {
			_hits += new_hits;
		}

		finally {
			_lock.unlock();
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
		_lock.lock();

		try {
			if (_jumps == null) {
				_jumps = new ArrayList();
			}

			if (_jumps.size() <= jumpNumber) {
				for (int i = _jumps.size(); i <= jumpNumber;
					_jumps.add(new JumpData(i++)));
			}

			return (JumpData) _jumps.get(jumpNumber);
		}
		finally {
			_lock.unlock();
		}
	}

	protected SwitchData getSwitchData(int switchNumber, SwitchData data) {
		_lock.lock();

		try {
			if (_switches == null) {
				_switches = new ArrayList();
			}

			if (_switches.size() < switchNumber) {
				for (int i = _switches.size(); i < switchNumber;
					 _switches.add(new SwitchData(i++)));
			}

			if (_switches.size() == switchNumber) {
				if (data != null) {
					_switches.add(data);
				}
				else {
					_switches.add(new SwitchData(switchNumber));
				}
			}

			return (SwitchData) _switches.get(switchNumber);
		}
		finally {
			_lock.unlock();
		}
	}

	protected void setMethodNameAndDescriptor(String name, String descriptor) {
		_lock.lock();

		try {
			_methodName = name;
			_methodDescriptor = descriptor;
		}
		finally {
			_lock.unlock();
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
				myLock = _lock.tryLock();
				otherLock = other._lock.tryLock();
			}
			finally {
				if ((!myLock) || (!otherLock))
				{
					//could not obtain both locks - so unlock the one we got.

					if (myLock) {
						_lock.unlock();
					}

					if (otherLock) {
						other._lock.unlock();
					}

					//do a yield so the other threads will get to work.

					Thread.yield();
				}
			}
		}
	}

	private void initLock() {
		_lock = new ReentrantLock();
	}

	private void readObject(ObjectInputStream in)
		throws ClassNotFoundException, IOException {

		in.defaultReadObject();
		initLock();
	}

	private static final long serialVersionUID = 4;

	private long _hits;
	private List _jumps;
	private int _lineNumber;
	private transient Lock _lock;
	private String _methodDescriptor;
	private String _methodName;
	private List _switches;

}