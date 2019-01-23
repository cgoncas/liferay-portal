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

package com.liferay.category.apio.internal.architect.router.test;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.category.apio.architect.model.Category;
import com.liferay.category.apio.internal.architect.resource.test.BaseCategoryNestedCollectionResourceTest;
import com.liferay.category.apio.internal.architect.resource.test.model.CategoryImpl;
import com.liferay.portal.apio.test.util.PaginationRequest;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class CategoryCategoryNestedCollectionResourceTest
	extends BaseCategoryNestedCollectionResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetVocabulary = AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
			_group.getGroupId());
	}

	@Test
	public void testAddAssetCategory() throws Exception {
		String name = RandomTestUtil.randomString(10);
		String description = RandomTestUtil.randomString(10);

		AssetCategory assetCategory = addAssetCategory(
			_assetVocabulary.getVocabularyId(),
			new CategoryImpl(name, description));

		Assert.assertNotNull(assetCategory.getCreateDate());
		Assert.assertEquals(
			description, assetCategory.getDescription(LocaleUtil.getDefault()));
		Assert.assertNotNull(assetCategory.getModifiedDate());
		Assert.assertEquals(name, assetCategory.getName());
	}

	@Test
	public void testGetAssetCategory() throws Exception {
		String name = RandomTestUtil.randomString(10);
		String description = RandomTestUtil.randomString(10);

		AssetCategory assetCategory = addAssetCategory(
			_assetVocabulary.getVocabularyId(),
			new CategoryImpl(name, description));

		AssetCategory assetCategoryRetrieved = getAssetCategory(
			assetCategory.getCategoryId());

		Assert.assertEquals(
			description,
			assetCategoryRetrieved.getDescription(LocaleUtil.getDefault()));
		Assert.assertEquals(name, assetCategoryRetrieved.getName());
	}

	/**
	 * Test get page items.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGetPageItems() throws Exception {
		String name = RandomTestUtil.randomString(10);
		String description = RandomTestUtil.randomString(10);

		addAssetCategory(
			_assetVocabulary.getVocabularyId(),
			new CategoryImpl(name, description));

		PageItems<AssetCategory> pageItems = getPageItems(
			PaginationRequest.of(10, 1), _assetVocabulary.getVocabularyId());

		Assert.assertEquals(1, pageItems.getTotalCount());

		List<AssetCategory> assetCategories =
			(List<AssetCategory>)pageItems.getItems();

		Assert.assertEquals(
			assetCategories.toString(), 1, assetCategories.size());

		AssetCategory assetCategory = assetCategories.get(0);

		Assert.assertEquals(
			description, assetCategory.getDescription(LocaleUtil.getDefault()));
		Assert.assertEquals(name, assetCategory.getName());
	}

	@Test
	public void testUpdateAssetCategory() throws Exception {
		Category category = new CategoryImpl(
			RandomTestUtil.randomString(10), RandomTestUtil.randomString(10));

		AssetCategory assetCategory = addAssetCategory(
			_assetVocabulary.getVocabularyId(), category);

		String name = RandomTestUtil.randomString(10);
		String description = RandomTestUtil.randomString(10);

		AssetCategory updatedAssetCategory = updateAssetCategory(
			assetCategory.getCategoryId(), new CategoryImpl(name, description));

		Assert.assertEquals(
			description,
			updatedAssetCategory.getDescription(LocaleUtil.getDefault()));

		Assert.assertEquals(name, updatedAssetCategory.getName());
	}

	private AssetVocabulary _assetVocabulary;

	@DeleteAfterTestRun
	private Group _group;

}