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

package com.liferay.shopping.upgrade.v1_0_0;

import com.liferay.portal.upgrade.v7_0_0.UpgradeKernelPackage;
import com.liferay.shopping.model.ShoppingCart;
import com.liferay.shopping.model.ShoppingCartItem;
import com.liferay.shopping.model.ShoppingCategory;
import com.liferay.shopping.model.ShoppingCoupon;
import com.liferay.shopping.model.ShoppingItem;
import com.liferay.shopping.model.ShoppingItemField;
import com.liferay.shopping.model.ShoppingItemPrice;
import com.liferay.shopping.model.ShoppingOrder;
import com.liferay.shopping.model.ShoppingOrderItem;

/**
 * @author Philip Jones
 */
public class UpgradeClassNames extends UpgradeKernelPackage {

	@Override
	protected String[][] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String[][] getResourceNames() {
		return _RESOURCE_NAMES;
	}

	private static final String[][] _CLASS_NAMES = new String[][] {
		{
			"com.liferay.portlet.shopping.model.ShoppingCart",
			ShoppingCart.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingCartItem",
			ShoppingCartItem.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingCategory",
			ShoppingCategory.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingCoupon",
			ShoppingCoupon.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingItem",
			ShoppingItem.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingItemField",
			ShoppingItemField.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingItemPrice",
			ShoppingItemPrice.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingOrder",
			ShoppingOrder.class.getName()
		},
		{
			"com.liferay.portlet.shopping.model.ShoppingOrderItem",
			ShoppingOrderItem.class.getSimpleName()
		}
	};

	private static final String[][] _RESOURCE_NAMES = new String[][] {
		{
			"com.liferay.portlet.shopping", "com.liferay.shopping"
		}
	};

}