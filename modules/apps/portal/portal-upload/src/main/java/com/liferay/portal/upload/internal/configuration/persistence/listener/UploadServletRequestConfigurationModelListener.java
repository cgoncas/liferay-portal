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

package com.liferay.portal.upload.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upload.internal.configuration.UploadServletRequestConfiguration;

import java.io.File;

import java.util.Dictionary;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "model.class.name=com.liferay.portal.upload.internal.configuration.UploadServletRequestConfiguration",
	service = ConfigurationModelListener.class
)
public class UploadServletRequestConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		Object maxSizeObject = properties.get("maxSize");

		if (maxSizeObject != null) {
			long maxSize = (long)maxSizeObject;

			if (maxSize < _MINIMUM_MAX_SIZE) {
				ResourceBundle resourceBundle = _getResourceBundle();

				throw new ConfigurationModelListenerException(
					_language.format(
						resourceBundle,
						"the-maximum-upload-request-size-cannot-be-less-than-x",
						_language.formatStorageSize(
							GetterUtil.getDouble(_MINIMUM_MAX_SIZE),
							resourceBundle.getLocale())),
					UploadServletRequestConfiguration.class, getClass(),
					properties);
			}
		}

		String tempDir = (String)properties.get("tempDir");

		if (Validator.isNotNull(tempDir)) {
			File tempFile = new File(tempDir);

			if (!tempFile.exists()) {
				throw new ConfigurationModelListenerException(
					ResourceBundleUtil.getString(
						_getResourceBundle(),
						"please-enter-a-valid-temporary-storage-directory"),
					UploadServletRequestConfiguration.class, getClass(),
					properties);
			}
		}
	}

	private ResourceBundle _getResourceBundle() {
		return ResourceBundleUtil.getBundle(
			LocaleThreadLocal.getThemeDisplayLocale(), getClass());
	}

	private static final long _MINIMUM_MAX_SIZE = 1024 * 100;

	@Reference
	private Language _language;

}