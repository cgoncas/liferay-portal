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

package com.liferay.structured.content.apio.internal.architect.form;

import com.liferay.apio.architect.form.Form;
import com.liferay.category.apio.architect.identifier.CategoryIdentifier;
import com.liferay.structure.apio.architect.identifier.ContentStructureIdentifier;
import com.liferay.structured.content.apio.architect.model.StructuredContent;
import com.liferay.structured.content.apio.architect.model.StructuredContentValue;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the values extracted from a structured content form.
 *
 * @author Alejandro Hernández
 */
public class StructuredContentForm implements StructuredContent {

	/**
	 * Builds a {@code Form} that generates a {@code StructuredContentForm}
	 * that depends on the HTTP body.
	 *
	 * @param  formBuilder the form builder
	 * @return the form
	 */
	public static Form<StructuredContentForm> buildForm(
		Form.Builder<StructuredContentForm> formBuilder) {

		return formBuilder.title(
			__ -> "The structured content creator/update form"
		).description(
			__ -> "This form can be used to create/update a structured content"
		).constructor(
			StructuredContentForm::new
		).addOptionalDate(
			"datePublished", StructuredContentForm::setPublishedDate
		).addOptionalLinkedModelList(
			"category", CategoryIdentifier.class,
			StructuredContentForm::setCategories
		).addOptionalNestedModelList(
			"values", StructuredContentValueForm::buildForm,
			StructuredContentForm::setStructuredContentValues
		).addOptionalString(
			"description", StructuredContentForm::setDescription
		).addOptionalStringList(
			"keywords", StructuredContentForm::setKeywords
		).addRequiredLinkedModel(
			"contentStructure", ContentStructureIdentifier.class,
			StructuredContentForm::setContentStructureId
		).addRequiredString(
			"title", StructuredContentForm::setTitle
		).build();
	}

	@Override
	public List<Long> getCategories() {
		return _categories;
	}

	@Override
	public Long getContentStructureId() {
		return _contentStructureId;
	}

	@Override
	public Optional<Map<Locale, String>> getDescriptionMapOptional(
		Locale locale) {

		return _getStringMapOptional(locale, _description);
	}

	@Override
	public List<String> getKeywords() {
		return _keywords;
	}

	@Override
	public Optional<LocalDateTime> getPublishedDateOptional() {
		return Optional.ofNullable(
			_publishedDate
		).map(
			date -> LocalDateTime.ofInstant(
				date.toInstant(), ZoneId.systemDefault())
		);
	}

	@Override
	public List<? extends StructuredContentValue> getStructuredContentValues() {
		return _structuredContentValues;
	}

	@Override
	public Map<Locale, String> getTitleMap(Locale locale) {
		Map<Locale, String> titleMap = new HashMap<>();

		titleMap.put(locale, _title);

		return titleMap;
	}

	public void setCategories(List<Long> categories) {
		_categories = categories;
	}

	public void setContentStructureId(Long contentStructureId) {
		_contentStructureId = contentStructureId;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setKeywords(List<String> keywords) {
		_keywords = keywords;
	}

	public void setPublishedDate(Date publishedDate) {
		if (publishedDate != null) {
			_publishedDate = new Date(publishedDate.getTime());
		}
		else {
			_publishedDate = null;
		}
	}

	public void setStructuredContentValues(
		List<? extends StructuredContentValue> structuredContentValues) {

		_structuredContentValues = structuredContentValues;
	}

	public void setTitle(String title) {
		_title = title;
	}

	private Optional<Map<Locale, String>> _getStringMapOptional(
		Locale locale, String value) {

		return Optional.ofNullable(
			value
		).map(
			description -> {
				Map<Locale, String> map = new HashMap<>();

				map.put(locale, value);

				return map;
			}
		);
	}

	private List<Long> _categories;
	private Long _contentStructureId;
	private String _description;
	private List<String> _keywords;
	private Date _publishedDate;
	private List<? extends StructuredContentValue> _structuredContentValues =
		new ArrayList<>();
	private String _title;

}