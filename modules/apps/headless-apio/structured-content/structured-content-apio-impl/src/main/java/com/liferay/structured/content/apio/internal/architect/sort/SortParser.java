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

package com.liferay.structured.content.apio.internal.architect.sort;

import com.liferay.petra.string.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * Utility for parsing Sort expressions. It uses a model to create a list of
 * {@link SortKey}.
 *
 * @author Cristina González
 * @review
 */
@Component(immediate = true, service = SortParser.class)
public class SortParser {

	/**
	 * Returns a List of  {@link SortKey} obtained from a comma-separated list
	 * of field names and sort directions.
	 *
	 * Sort directions supported are desc and asc and can be appended to each
	 * sort field, separated by the ':' character.
	 *
	 * If a sort direction is not provided, the sort ket will be 'asc'.
	 *
	 * For example:
	 * - field1,field2,field3
	 * - field1:asc,field2:desc,field3
	 * - field1:asc,field2,field3:desc
	 *
	 * @param sortExpressions - String to be parsed
	 * @return a  {@link List<SortKey>}
	 * @review
	 */
	public List<SortKey> parse(String sortExpressions) {
		if (sortExpressions == null) {
			return Collections.emptyList();
		}

		List<String> sortExpressionsList = StringUtil.split(sortExpressions);

		Stream<String> stream = sortExpressionsList.stream();

		return stream.map(
			this::getSortKey
		).flatMap(
			sortKeyOptional ->
				sortKeyOptional.map(Stream::of).orElseGet(Stream::empty)
		).collect(
			Collectors.toList()
		);
	}

	public static class SortKey {

		public SortKey(String fieldName, boolean asc) {
			_fieldName = fieldName;
			_asc = asc;
		}

		public String getFieldName() {
			return _fieldName;
		}

		public boolean isAsc() {
			return _asc;
		}

		private final boolean _asc;
		private final String _fieldName;

	}

	protected Optional<SortKey> getSortKey(String sortExpression) {
		List<String> sortParts = StringUtil.split(sortExpression, ':');

		if (sortParts.isEmpty()) {
			return Optional.empty();
		}

		if (sortParts.size() > 2) {
			throw new RuntimeException("Unable to parse sort expression");
		}

		String fieldName = sortParts.get(0);

		boolean asc = _DEFAULT_ASC;

		if (sortParts.size() > 1) {
			asc = isAsc(sortParts.get(1));
		}

		return Optional.of(new SortKey(fieldName, asc));
	}

	protected boolean isAsc(String orderBy) {
		if (orderBy == null) {
			return _DEFAULT_ASC;
		}

		if (com.liferay.portal.kernel.util.StringUtil.equals(
				com.liferay.portal.kernel.util.StringUtil.toLowerCase(orderBy),
				_ORDER_BY_ASC)) {

			return true;
		}

		if (com.liferay.portal.kernel.util.StringUtil.equals(
				com.liferay.portal.kernel.util.StringUtil.toLowerCase(orderBy),
				_ORDER_BY_DESC)) {

			return false;
		}

		return _DEFAULT_ASC;
	}

	private static final boolean _DEFAULT_ASC = true;

	private static final String _ORDER_BY_ASC = "asc";

	private static final String _ORDER_BY_DESC = "desc";

}