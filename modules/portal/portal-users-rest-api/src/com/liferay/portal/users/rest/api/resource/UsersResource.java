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

package com.liferay.portal.users.rest.api.resource;

import static com.liferay.portal.users.rest.provider.OptionalBodyWriter.createEmptyResponse;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.Image;
import com.liferay.portal.model.ListType;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.User;
import com.liferay.portal.model.Website;
import com.liferay.portal.security.auth.FullNameDefinition;
import com.liferay.portal.security.auth.FullNameDefinitionFactory;
import com.liferay.portal.security.auth.FullNameField;
import com.liferay.portal.service.ImageService;
import com.liferay.portal.service.ListTypeLocalServiceUtil;
import com.liferay.portal.service.ListTypeService;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserService;
import com.liferay.portal.users.rest.api.model.NewRestUser;
import com.liferay.portal.users.rest.api.model.Optional;
import com.liferay.portal.users.rest.api.model.Page;
import com.liferay.portal.users.rest.api.model.Pagination;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.announcements.model.AnnouncementsDelivery;

import java.io.IOException;
import java.io.InputStream;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.apache.commons.io.IOUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true, service = UsersResource.class
)
public class UsersResource {

	@POST
	public Response createUser(@Context Company company, NewRestUser restUser)
		throws PortalException {

		String prefix = restUser.getPrefix();

		ListType prefixListType = ListTypeLocalServiceUtil.addListType(
			prefix, ListTypeConstants.CONTACT_PREFIX);

		long prefixId = prefixListType.getListTypeId();

		String suffix = restUser.getPrefix();

		ListType suffixListType = ListTypeLocalServiceUtil.addListType(
			suffix, ListTypeConstants.CONTACT_SUFFIX);

		long suffixId = suffixListType.getListTypeId();

		String password = restUser.getPassword();

		Calendar calendar = GregorianCalendar.getInstance();

		Date birthDay = restUser.getBirthDay();

		if (birthDay != null) {
			calendar.setTime(birthDay);
		}

		User user = _userService.addUser(
			company.getCompanyId(), true, password, password, false,
			restUser.getScreenName(), restUser.getEmailAddress(), 0, null,
			Locale.forLanguageTag(restUser.getLanguageId()),
			restUser.getFirstName(), restUser.getMiddleName(),
			restUser.getLastName(), prefixId, suffixId, restUser.isMale(),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), restUser.getJobTitle(), null, null,
			null, null, Collections.<Address>emptyList(),
			Collections.<EmailAddress>emptyList(),
			Collections.<Phone>emptyList(), Collections.<Website>emptyList(),
			Collections.<AnnouncementsDelivery>emptyList(), false,
			new ServiceContext());

		return Response.status(201).entity(user).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteUserById(@PathParam("id") long id)
		throws PortalException {

		try {
			_userService.deleteUser(id);

			return Response.status(204).build();
		}
		catch (NoSuchUserException nsue) {
			return Response.status(204).build();
		}
	}

	@GET
	@Path("/{id}/portrait")
	public Response getPortrait(@PathParam("id") long userId)
		throws PortalException {

		try {
			User user = _userService.getUserById(userId);

			Image image = _imageService.getImage(user.getPortraitId());

			if (image == null) {
				return createEmptyResponse("portrait");
			}

			return Response.
					ok(image.getTextObj()).
					type("image/" + image.getType()).build();
		}
		catch (NoSuchUserException nsue) {
			return createEmptyResponse("user");
		}
	}

	@GET
	@Path("/{id}")
	public Optional<User> getUser(@PathParam("id") long id)
		throws PortalException {

		try {
			return Optional.of(_userService.getUserById(id));
		}
		catch (NoSuchUserException e) {
			return Optional.empty();
		}
	}

	@GET
	@Path("/prefixes")
	public String[] getUserPrefixes(
			@Context Company company, @Context Request request)
		throws PortalException {

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		Locale[] languages = availableLocales.toArray(new Locale[] {});

		List<Variant> variants = Variant.
			mediaTypes(
					MediaType.APPLICATION_JSON_TYPE,
					MediaType.APPLICATION_XML_TYPE).
			languages(languages).add().build();

		Variant variant = request.selectVariant(variants);

		Locale locale = null;

		if (variant == null) {
			locale = PortalUtil.getSiteDefaultLocale(company.getGroupId());
		}
		else {
			locale = variant.getLanguage();
		}

		FullNameDefinition fullNameDefinition =
			FullNameDefinitionFactory.getInstance(locale);

		List<FullNameField> fullNameFields =
			fullNameDefinition.getFullNameFields();

		FullNameField prefixNameField = null;

		for (FullNameField fullNameField : fullNameFields) {
			if (fullNameField.getName().equals("prefix")) {
				prefixNameField = fullNameField;

				break;
			}
		}

		if (prefixNameField != null) {
			return prefixNameField.getValues();
		}

		return null;
	}

	@GET
	public Page<User> listUsers(
			@Context Company company, @Context Pagination pagination)
		throws PortalException {

		long companyId = company.getCompanyId();

		return pagination.createPage(
			_userService.getCompanyUsers(
				companyId, pagination.getStartPosition(),
				pagination.getEndPosition()),
			_userService.getCompanyUsersCount(companyId));
	}

	@Reference
	public void setImageService(ImageService imageService) {
		_imageService = imageService;
	}

	@Reference
	public void setListTypeService(ListTypeService listTypeService) {
		_listTypeService = listTypeService;
	}

	@Reference
	public void setUserService(UserService userService) {
		_userService = userService;
	}

	@Consumes("image/*")
	@PUT
	@Path("/{id}/portrait")
	public Response updatePortrait(
			@PathParam("id") long userId, InputStream inputStream)
		throws IOException, PortalException {

		try {
			_userService.updatePortrait(
				userId, IOUtils.toByteArray(inputStream));

			return Response.ok().build();
		}
		catch (NoSuchUserException nsue) {
			return createEmptyResponse("user");
		}
	}

	private ImageService _imageService;
	private ListTypeService _listTypeService;
	private UserService _userService;

}