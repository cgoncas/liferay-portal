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

package com.liferay.category.apio.internal.architect.resource.test;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.category.apio.architect.model.Category;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Rubén Pulido
 */
public class BaseCategoryNestedCollectionResourceTest {

	protected AssetCategory addAssetCategory(
			long vocabularyId, Category category)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_addAssetCategory", long.class, Category.class);

		method.setAccessible(true);

		return (AssetCategory)method.invoke(
			getNestedCollectionResource(), vocabularyId, category);
	}

	protected AssetCategory getAssetCategory(long assetCategoryId)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getAssetCategory", long.class);

		method.setAccessible(true);

		return (AssetCategory)method.invoke(
			getNestedCollectionResource(), assetCategoryId);
	}

	protected NestedCollectionResource getNestedCollectionResource()
		throws Exception {

		Registry registry = RegistryUtil.getRegistry();

		Collection<NestedCollectionResource> collection = registry.getServices(
			NestedCollectionResource.class,
			"(component.name=com.liferay.category.apio.internal.architect." +
				"resource.CategoryNestedCollectionResource)");

		Iterator<NestedCollectionResource> iterator = collection.iterator();

		return iterator.next();
	}

	protected PageItems<AssetCategory> getPageItems(
			Pagination pagination, long groupId)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getPageItems", Pagination.class, long.class);

		method.setAccessible(true);

		return (PageItems)method.invoke(
			getNestedCollectionResource(), pagination, groupId);
	}

	protected AssetCategory updateAssetCategory(
			long assetCategoryId, Category category)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_updateAssetCategory", long.class, Category.class);

		method.setAccessible(true);

		return (AssetCategory)method.invoke(
			getNestedCollectionResource(), assetCategoryId, category);
	}

}