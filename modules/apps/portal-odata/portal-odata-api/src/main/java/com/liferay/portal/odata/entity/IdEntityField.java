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

package com.liferay.portal.odata.entity;

import java.util.Locale;
import java.util.function.Function;

/**
 * Models an date entity field. A Entity field with a type {@code
 * EntityField.Type.ID}
 *
 * @author Cristina González
 * @review
 */
public class IdEntityField extends EntityField {

	/**
	 * Creates a new {@code IdEntityField} with a {@code Function} to convert
	 * the entity field's name to a filterable/sortable field name for a locale.
	 *
	 * @param  name the entity field's name
	 * @param  filterableAndSortableFieldNameFunction the {@code Function} that
	 *         converts the entity field's name to a filterable/sortable field
	 *         name for a locale
	 * @param  filterableFieldValueFunction the {@code Function} that converts
	 *         the entity field's value to a filterable field value
	 * @review
	 */
	public IdEntityField(
		String name,
		Function<Locale, String> filterableAndSortableFieldNameFunction,
		Function<Object, String> filterableFieldValueFunction) {

		super(
			name, Type.ID, filterableAndSortableFieldNameFunction,
			filterableAndSortableFieldNameFunction,
			filterableFieldValueFunction);
	}

}