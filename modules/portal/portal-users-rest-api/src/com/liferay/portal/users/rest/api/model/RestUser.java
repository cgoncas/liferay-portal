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

package com.liferay.portal.users.rest.api.model;

import com.liferay.portal.model.User;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos Sierra Andrés
 */
@XmlRootElement
public class RestUser {

	public RestUser() {
	}

	public RestUser(User user) {
		_user = user;
	}

	public long getContactId() {
		return _user.getContactId();
	}

	public String getEmailAddress() {
		return _user.getEmailAddress();
	}

	public String getFirstName() {
		return _user.getFirstName();
	}

	public String getGreeting() {
		return _user.getGreeting();
	}

	public String getJobTitle() {
		return _user.getJobTitle();
	}

	public String getLanguageId() {
		return _user.getLanguageId();
	}

	public String getLastName() {
		return _user.getLastName();
	}

	public String getMiddleName() {
		return _user.getMiddleName();
	}

	public ResourceLink getPortraitLink() {
		return _portraitLink;
	}

	public String getScreenName() {
		return _user.getScreenName();
	}

	public int getStatus() {
		return _user.getStatus();
	}

	public long getUserId() {
		return _user.getUserId();
	}

	public void setContactId(long contactId) {
		_user.setContactId(contactId);
	}

	public void setEmailAddress(String emailAddress) {
		_user.setEmailAddress(emailAddress);
	}

	public void setFirstName(String firstName) {
		_user.setFirstName(firstName);
	}

	public void setGreeting(String greeting) {
		_user.setGreeting(greeting);
	}

	public void setJobTitle(String jobTitle) {
		_user.setJobTitle(jobTitle);
	}

	public void setLanguageId(String languageId) {
		_user.setLanguageId(languageId);
	}

	public void setLastName(String lastName) {
		_user.setLastName(lastName);
	}

	public void setMiddleName(String middleName) {
		_user.setMiddleName(middleName);
	}

	public void setPortraitLink(ResourceLink portraitLink) {
		_portraitLink = portraitLink;
	}

	public void setScreenName(String screenName) {
		_user.setScreenName(screenName);
	}

	public void setStatus(int status) {
		_user.setStatus(status);
	}

	public void setUserId(long userId) {
		_user.setUserId(userId);
	}

	private ResourceLink _portraitLink;

	private User _user;

}