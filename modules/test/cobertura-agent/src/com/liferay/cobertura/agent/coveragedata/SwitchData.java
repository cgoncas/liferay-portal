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

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cristina González
 */
public class SwitchData implements BranchCoverageData, Comparable, Serializable,
		HasBeenInstrumented {

	public SwitchData(int switchNumber) {
		this(switchNumber, new int[0]);
	}

	public SwitchData(int switchNumber, int min, int max) {
		_switchNumber = switchNumber;
		_defaultHits = 0;
		_hits = new long[max - min + 1];
		Arrays.fill(_hits, 0);
		_keys = new int[max - min + 1];

		for (int i = 0; min <= max; _keys[i++] = min++);
		_initLock();
	}

	public SwitchData(int switchNumber, int[] keys) {
		_switchNumber = switchNumber;
		_defaultHits = 0;
		_hits = new long[keys.length];
		Arrays.fill(_hits, 0);
		_keys = new int[keys.length];
		System.arraycopy(keys, 0, _keys, 0, keys.length);
		_initLock();
	}

	public int compareTo(Object o) {
		if (!o.getClass().equals(SwitchData.class))return Integer.MAX_VALUE;
		return _switchNumber - ((SwitchData)o)._switchNumber;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || !obj.getClass().equals(getClass())) {
			return false;
		}

		SwitchData switchData = (SwitchData)obj;
		_getBothLocks(switchData);

		try {
			return (_defaultHits == switchData._defaultHits) &&
					 (Arrays.equals(_hits, switchData._hits)) &&
					 (_switchNumber == switchData._switchNumber);
		}
		finally {
			_lock.unlock();
			switchData._lock.unlock();
		}
	}

	public double getBranchCoverageRate() {
		_lock.lock();

		try {
			int branches = _hits.length + 1;
			int hit = (_defaultHits > 0) ? 1 : 0;

			for (int i =
				_hits.length - 1; i >= 0; hit += ((_hits[i--] > 0) ? 1 : 0));
			return ((double)hit) / branches;
		}
		finally {
			_lock.unlock();
		}
	}

	public long getDefaultHits() {
		_lock.lock();

		try {
			return _defaultHits;
		}
		finally {
			_lock.unlock();
		}
	}

	public long getHits(int branch) {
		_lock.lock();

		try {
			if (_hits.length > branch)return _hits[branch];
			return -1;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getNumberOfCoveredBranches() {
		_lock.lock();

		try {
			int ret = (_defaultHits > 0) ? 1 : 0;

			for (int i = _hits.length -1; i >= 0; i--) {
				if (_hits[i] > 0)ret++;
			}

			return ret;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getNumberOfValidBranches() {
		_lock.lock();

		try {
			return _hits.length + 1;
		}
		finally {
			_lock.unlock();
		}
	}

	public int getSwitchNumber() {
		return _switchNumber;
	}

	public int hashCode() {
		return _switchNumber;
	}

	public void merge(BranchCoverageData coverageData) {
		SwitchData switchData = (SwitchData)coverageData;
		_getBothLocks(switchData);

		try {
			_defaultHits += switchData._defaultHits;

			for (int i = Math.min(_hits.length, switchData._hits.length) - 1;
				 i >= 0; i--) {

				_hits[i] += switchData._hits[i];
			}

			if (switchData._hits.length > _hits.length) {
				long[] old = _hits;
				_hits = new long[switchData._hits.length];
				System.arraycopy(old, 0, _hits, 0, old.length);
				System.arraycopy(
					switchData._hits, old.length, _hits, old.length,
					_hits.length - old.length);
			}

			if ((_keys.length == 0) && (switchData._keys.length > 0))
				_keys = switchData._keys;
		}
		finally {
			_lock.unlock();
			switchData._lock.unlock();
		}
	}

	protected void touchBranch(int branch, int new_hits) {
		_lock.lock();

		try {
			if (branch == -1) {
				_defaultHits++;
			}
			else {
				if (_hits.length <= branch) {
					long[] old = _hits;
					_hits = new long[branch + 1];
					System.arraycopy(old, 0, _hits, 0, old.length);
					Arrays.fill(_hits, old.length, _hits.length - 1, 0);
				}

				_hits[branch]+= new_hits;
			}
		}
		finally {
			_lock.unlock();
		}
	}

	private void _getBothLocks(SwitchData other) {
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
				if (!myLock || !otherLock) {
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

	private void _initLock() {
		_lock = new ReentrantLock();
	}

	private void readObject(ObjectInputStream in)
		throws ClassNotFoundException, IOException {

		in.defaultReadObject();
		_initLock();
	}

	private static final long serialVersionUID = 9;

	private long _defaultHits;
	private long[] _hits;
	private int[] _keys;
	private transient Lock _lock;
	private int _switchNumber;

}