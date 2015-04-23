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

package com.liferay.cobertura.agent.coveragedata.countermaps;

import com.liferay.cobertura.agent.coveragedata.HasBeenInstrumented;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cristina González
 */
public class AtomicCounterMap<T> implements CounterMap<T>, HasBeenInstrumented {

	public synchronized Map<T, Integer> getFinalStateAndCleanIt() {
		Map<T, Integer> res = new LinkedHashMap<>();

		Iterator<Map.Entry<T, AtomicInteger>> iterator =
			counters.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<T, AtomicInteger> entry = iterator.next();

			T key = entry.getKey();

			int old = entry.getValue().get();

			iterator.remove();

			if (old > 0) {
				res.put(key, old);
			}
		}

		return res;
	}

	public int getSize() {
		return counters.size();
	}

	public final int getValue(T key) {
		AtomicInteger v = counters.get(key);

		if (v == null) {
			return 0;
		}

		return v.get();
	}

	public final void incrementValue(T key) {
		//AtomicInteger v=counters.putIfAbsent(key, new AtomicInteger(1));
		//return (v!=null)?v.incrementAndGet():1;

		AtomicInteger v = counters.get(key);

		if (v != null) {
			v.incrementAndGet();
		}
		else {
			v = counters.putIfAbsent(key, new AtomicInteger(1));

			if (v != null) {
				v.incrementAndGet();
			}
		}
	}

	public final void incrementValue(T key, int inc) {
		AtomicInteger v = counters.get(key);

		if (v!= null) {
			v.addAndGet(inc);
		}
		else {
			v = counters.putIfAbsent(key, new AtomicInteger(inc));

			if (v != null) {
				v.addAndGet(inc);
			}
		}
	}

	private final ConcurrentMap<T, AtomicInteger> counters =
		new ConcurrentHashMap<>();

}