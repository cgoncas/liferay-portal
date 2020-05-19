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

package com.liferay.content.dashboard.web.internal.info.item.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.content.dashboard.web.internal.display.ContentDashboardInfoItemDisplay;
import com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItemTracker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cristina González
 */
public class ContentDashboardInfoItemDisplayMapper {

	public ContentDashboardInfoItemDisplayMapper(
		ContentDashboardInfoItemTracker contentDashboardInfoItemTracker) {

		_contentDashboardInfoItemTracker = contentDashboardInfoItemTracker;
	}

	public Collection<String> getClassNames() {
		return _contentDashboardInfoItemTracker.getClassNames();
	}

	public List<ContentDashboardInfoItemDisplay<?>> map(
		List<AssetEntry> assetEntries) {

		Stream<AssetEntry> stream = assetEntries.stream();

		return stream.map(
			this::_toContentDashboardInfoItemDisplay
		).filter(
			optional -> optional.isPresent()
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	private <T> Optional<ContentDashboardInfoItemDisplay<T>>
		_toContentDashboardInfoItemDisplay(AssetEntry assetEntry) {

		AssetRendererFactory<T> assetRendererFactory =
			(AssetRendererFactory<T>)
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						assetEntry.getClassName());

		try {
			AssetRenderer<T> assetRenderer =
				assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());

			T assetObject = assetRenderer.getAssetObject();

			return Optional.of(
				ContentDashboardInfoItemDisplay.of(
					_contentDashboardInfoItemTracker.
						getContentDashboardInfoItem(assetRenderer),
					assetObject));
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return Optional.empty();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardInfoItemDisplayMapper.class);

	private final ContentDashboardInfoItemTracker
		_contentDashboardInfoItemTracker;

}