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

import javax.annotation.Generated;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@XmlRootElement(name = "Creator")
public class Creator {

	public String getAdditionalName() {
		return _additionalName;
	}

	public String getFamilyName() {
		return _familyName;
	}

	public String getGivenName() {
		return _givenName;
	}

	public Long getId() {
		return _id;
	}

	public String getImage() {
		return _image;
	}

	public String getName() {
		return _name;
	}

	public String getProfileURL() {
		return _profileURL;
	}

	public void setAdditionalName(String additionalName) {
		_additionalName = additionalName;
	}

	public void setFamilyName(String familyName) {
		_familyName = familyName;
	}

	public void setGivenName(String givenName) {
		_givenName = givenName;
	}

	public void setId(Long id) {
		_id = id;
	}

	public void setImage(String image) {
		_image = image;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setProfileURL(String profileURL) {
		_profileURL = profileURL;
	}

	private String _additionalName;
	private String _familyName;
	private String _givenName;
	private Long _id;
	private String _image;
	private String _name;
	private String _profileURL;

}