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

package com.liferay.application.list.taglib.display.context.logic;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.layoutconfiguration.util.RuntimePageUtil;
import com.liferay.portal.kernel.model.LayoutTemplateConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.DynamicServletRequestUtil;
import com.liferay.portlet.LiferayPortletUtil;

import java.io.Writer;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Adolfo Pérez
 */
public class PanelAppContentHelper {

	public PanelAppContentHelper(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
	}

	public boolean isValidPortletSelected() {
		if (getPortlet() == null) {
			return false;
		}

		return true;
	}

	public void writeContent(Writer writer) throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		String layoutTemplateId = "max";

		if (themeDisplay.isStatePopUp()) {
			layoutTemplateId = "pop_up";
		}

		Theme theme = themeDisplay.getTheme();

		String velocityTemplateId =
			theme.getThemeId() + LayoutTemplateConstants.STANDARD_SEPARATOR +
				layoutTemplateId;

		String content = LayoutTemplateLocalServiceUtil.getContent(
			layoutTemplateId, true, theme.getThemeId());

		if (Validator.isNotNull(velocityTemplateId) &&
			Validator.isNotNull(content)) {

			HttpServletRequest httpServletRequest =
				getOriginalHttpServletRequest(_httpServletRequest);

			StringBundler sb = RuntimePageUtil.getProcessedTemplate(
				httpServletRequest, _httpServletResponse, getPortletId(),
				velocityTemplateId, content);

			if (sb != null) {
				sb.writeTo(writer);
			}
		}
	}

	protected long getCompanyId() {
		if (_companyId == null) {
			ThemeDisplay themeDisplay = _getThemeDisplay();

			_companyId = themeDisplay.getCompanyId();
		}

		return _companyId;
	}

	protected HttpServletRequest getOriginalHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		LiferayPortletRequest liferayPortletRequest =
			LiferayPortletUtil.getLiferayPortletRequest(
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST));

		if (liferayPortletRequest != null) {
			return liferayPortletRequest.getOriginalHttpServletRequest();
		}

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		return DynamicServletRequestUtil.createDynamicServletRequest(
			originalHttpServletRequest, getPortlet(),
			httpServletRequest.getParameterMap(), false);
	}

	protected Portlet getPortlet() {
		if ((_portlet == null) && Validator.isNotNull(getPortletId())) {
			_portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(), getPortletId());
		}

		return _portlet;
	}

	protected String getPortletId() {
		if (_portletId == null) {
			_portletId = (String)_httpServletRequest.getAttribute(
				"liferay-application-list:application-content:portletId");
		}

		return _portletId;
	}

	private ThemeDisplay _getThemeDisplay() {
		return (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private Long _companyId;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private Portlet _portlet;
	private String _portletId;

}