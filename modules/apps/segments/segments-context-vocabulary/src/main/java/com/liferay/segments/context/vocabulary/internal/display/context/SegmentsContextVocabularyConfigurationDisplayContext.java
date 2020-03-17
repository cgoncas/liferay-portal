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

package com.liferay.segments.context.vocabulary.internal.display.context;

import com.liferay.configuration.admin.definition.ConfigurationFieldOptionsProvider;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionURL;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Cristina González
 */
public class SegmentsContextVocabularyConfigurationDisplayContext {

	public SegmentsContextVocabularyConfigurationDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		ExtendedObjectClassDefinition extendedObjectClassDefinition,
		List<ConfigurationFieldOptionsProvider.Option> assetVocabularyOptions,
		List<ConfigurationFieldOptionsProvider.Option> entityFieldOptions) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_extendedObjectClassDefinition = extendedObjectClassDefinition;
		_assetVocabularyOptions = assetVocabularyOptions;
		_entityFieldOptions = entityFieldOptions;

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_locale = themeDisplay.getLocale();
	}

	public ActionURL getActionURL() {
		ActionURL actionURL = _renderResponse.createActionURL();

		actionURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/update_segments_context_vocabulary_configuration");
		actionURL.setParameter("redirect", String.valueOf(getRedirect()));

		return actionURL;
	}

	public List<ConfigurationFieldOptionsProvider.Option>
		getAssetVocabularyOptions() {

		return _assetVocabularyOptions;
	}

	public String getDescription() {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			_locale, getClass());

		return LanguageUtil.get(
			resourceBundle, _extendedObjectClassDefinition.getDescription());
	}

	public List<ConfigurationFieldOptionsProvider.Option>
		getEntityFieldOptions() {

		return _entityFieldOptions;
	}

	public String getFactoryPid() {
		return ParamUtil.getString(_renderRequest, "factoryPid");
	}

	public String getPid() {
		return ParamUtil.getString(_renderRequest, "pid");
	}

	public PortletURL getRedirect() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/view_configuration_screen");
		portletURL.setParameter(
			"configurationScreenKey",
			"segments-context-vocabulary-configuration-name");

		return portletURL;
	}

	private final List<ConfigurationFieldOptionsProvider.Option>
		_assetVocabularyOptions;
	private final List<ConfigurationFieldOptionsProvider.Option>
		_entityFieldOptions;
	private final ExtendedObjectClassDefinition _extendedObjectClassDefinition;
	private final Locale _locale;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}