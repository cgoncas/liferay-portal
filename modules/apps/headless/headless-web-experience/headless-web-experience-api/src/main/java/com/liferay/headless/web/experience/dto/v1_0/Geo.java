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

package com.liferay.headless.web.experience.dto.v1_0;

import com.liferay.petra.function.UnsafeSupplier;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

import javax.annotation.Generated;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("Geo")
@XmlRootElement(name = "Geo")
public class Geo {

	public Long getId() {
		return id;
	}

	public Number getLatitude() {
		return latitude;
	}

	public Number getLongitude() {
		return longitude;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Throwable> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
	}
		catch (Throwable t) {
			throw new RuntimeException(t);
	}
	}

	public void setLatitude(Number latitude) {
		this.latitude = latitude;
	}

	public void setLatitude(
		UnsafeSupplier<Number, Throwable> latitudeUnsafeSupplier) {

		try {
			latitude = latitudeUnsafeSupplier.get();
	}
		catch (Throwable t) {
			throw new RuntimeException(t);
	}
	}

	public void setLongitude(Number longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(
		UnsafeSupplier<Number, Throwable> longitudeUnsafeSupplier) {

		try {
			longitude = longitudeUnsafeSupplier.get();
	}
		catch (Throwable t) {
			throw new RuntimeException(t);
	}
	}

	@GraphQLField
	protected Long id;

	@GraphQLField
	protected Number latitude;

	@GraphQLField
	protected Number longitude;

}