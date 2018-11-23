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
 * Models a collection entity field.
 *
 * @author Ruben Pulido
 * @review
 */
public class CollectionEntityField extends EntityField {

//	/**
//	 * Creates a new {@code CollectionEntityField} with separate functions for
//	 * converting the entity field's name to a sortable and filterable field
//	 * name for a locale.
//	 *
//	 * @param  name the entity field's name
//	 * @param  type the type
//	 * @param  sortableFieldNameFunction the sortable field name {@code
//	 *         Function}
//	 * @param  filterableFieldNameFunction the filterable field name {@code
//	 *         Function}
//	 * @param  filterableFieldValueFunction the filterable field value {@code
//	 *         Function}
//	 * @review
//	 */
//	public CollectionEntityField(
////		String name, Type type,
////		Function<Locale, String> sortableFieldNameFunction,
////		Function<Locale, String> filterableFieldNameFunction,
////		Function<Object, String> filterableFieldValueFunction) {
////		super(name, type, sortableFieldNameFunction,
////			filterableFieldNameFunction, filterableFieldValueFunction);
////	}

	public CollectionEntityField(EntityField entityField) {

		super(
			entityField.getName(), Type.COLLECTION,
			locale -> entityField.getName(), locale -> entityField.getName(),
			String::valueOf);

		_entityField = entityField;
	}

	@Override
	public String toString() {
		return super.toString();
	}


	private EntityField _entityField;

	public EntityField getEntityField() {
		return _entityField;
	}
}