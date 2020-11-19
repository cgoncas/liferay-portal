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

package com.liferay.segments.web.internal.odata;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;
import com.liferay.portal.odata.filter.expression.UnaryExpression;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cristina González
 */
public class ExpressionVisitorImpl implements ExpressionVisitor<Object> {

	public ExpressionVisitorImpl(int groupCount, EntityModel entityModel) {
		_groupCount = groupCount;
		_entityModel = entityModel;
	}

	@Override
	public Object visitBinaryExpressionOperation(
		BinaryExpression.Operation operation, Object left, Object right) {

		if (Objects.equals(BinaryExpression.Operation.AND, operation) ||
			Objects.equals(BinaryExpression.Operation.OR, operation)) {

			return _getConjunctionJSONObject(
				operation, (JSONObject)left, (JSONObject)right);
		}
		else if (Objects.equals(BinaryExpression.Operation.EQ, operation) ||
				 Objects.equals(BinaryExpression.Operation.GE, operation) ||
				 Objects.equals(BinaryExpression.Operation.GT, operation) ||
				 Objects.equals(BinaryExpression.Operation.LE, operation) ||
				 Objects.equals(BinaryExpression.Operation.LT, operation) ||
				 Objects.equals(BinaryExpression.Operation.NE, operation)) {

			return _getOperationJSONObject(
				String.valueOf(operation), (EntityField)left,
				Collections.singletonList(right));
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitBinaryExpressionOperation with " +
				"operation " + operation);
	}

	@Override
	public Object visitListExpressionOperation(
		ListExpression.Operation operation, Object left, List<Object> right) {

		if (operation == ListExpression.Operation.IN) {
			return _getOperationJSONObject(
				String.valueOf(operation), (EntityField)left, right);
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitListExpressionOperation with operation " +
				operation);
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression) {
		return StringUtil.unquote(literalExpression.getText());
	}

	@Override
	public Object visitMemberExpression(MemberExpression memberExpression)
		throws ExpressionVisitException {

		Expression expression = memberExpression.getExpression();

		return expression.accept(this);
	}

	@Override
	public Object visitMethodExpression(
		List<Object> expressions, MethodExpression.Type type) {

		if (type == MethodExpression.Type.CONTAINS) {
			if (expressions.size() != 2) {
				throw new UnsupportedOperationException(
					StringBundler.concat(
						"Unsupported method visitMethodExpression with method",
						"type ", type, " and ", expressions.size(), "params"));
			}

			return _getOperationJSONObject(
				String.valueOf(type), (EntityField)expressions.get(0),
				Collections.singletonList(expressions.get(1)));
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitMethodExpression with method type " +
				type);
	}

	@Override
	public Object visitPrimitivePropertyExpression(
		PrimitivePropertyExpression primitivePropertyExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		return entityFieldsMap.get(primitivePropertyExpression.getName());
	}

	@Override
	public JSONObject visitUnaryExpressionOperation(
		UnaryExpression.Operation operation, Object operand) {

		if (Objects.equals(UnaryExpression.Operation.NOT, operation)) {
			JSONObject jsonObject = (JSONObject)operand;

			jsonObject.put(
				"operatorName",
				StringUtil.lowerCase(
					UnaryExpression.Operation.NOT + "-" +
						jsonObject.getString("operatorName")));

			return jsonObject;
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitUnaryExpressionOperation with operation " +
				operation);
	}

	private JSONObject _getConjunctionJSONObject(
		BinaryExpression.Operation operation, JSONObject leftJSONObject,
		JSONObject rightJSONObject) {

		String conjunctionName = leftJSONObject.getString("conjunctionName");

		if (Validator.isNotNull(conjunctionName) &&
			Objects.equals(conjunctionName, operation.toString())) {

			JSONArray jsonArray = leftJSONObject.getJSONArray("items");

			jsonArray.put(rightJSONObject);

			return JSONUtil.put(
				"conjunctionName",
				StringUtil.lowerCase(String.valueOf(operation))
			).put(
				"groupId", leftJSONObject.getString("groupId")
			).put(
				"items", jsonArray
			);
		}

		_groupCount++;

		return JSONUtil.put(
			"conjunctionName", operation
		).put(
			"groupId", "group_" + _groupCount
		).put(
			"items", JSONUtil.putAll(leftJSONObject, rightJSONObject)
		);
	}

	private JSONObject _getOperationJSONObject(
		String operatorName, EntityField entityField,
		List<Object> fieldValues) {

		Stream<Object> stream = fieldValues.stream();

		return JSONUtil.put(
			"operatorName", StringUtil.lowerCase(operatorName)
		).put(
			"propertyName", entityField.getName()
		).put(
			"value",
			stream.map(
				String::valueOf
			).collect(
				Collectors.joining(StringPool.COMMA)
			)
		);
	}

	private final EntityModel _entityModel;
	private int _groupCount;

}