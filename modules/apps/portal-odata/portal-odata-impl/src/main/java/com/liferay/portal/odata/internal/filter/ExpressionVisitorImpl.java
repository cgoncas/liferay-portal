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

package com.liferay.portal.odata.internal.filter;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.ComplexPropertyExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.LambdaFunctionExpression;
import com.liferay.portal.odata.filter.expression.LambdaVariableExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;

import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Julio Camarero
 */
public class ExpressionVisitorImpl implements ExpressionVisitor<Object> {

	public ExpressionVisitorImpl(
		Format format, Locale locale, EntityModel entityModel) {

		_format = format;
		_locale = locale;
		_entityModel = entityModel;
	}

	@Override
	public Filter visitBinaryExpressionOperation(
		BinaryExpression.Operation operation, Object left, Object right) {

		Optional<Filter> filterOptional = _getFilterOptional(
			operation, left, right, _locale);

		return filterOptional.orElseThrow(
			() -> new UnsupportedOperationException(
				"Unsupported method visitBinaryExpressionOperation with " +
					"operation " + operation));
	}

	@Override
	public Object visitComplexPropertyExpression(
		ComplexPropertyExpression complexPropertyExpression) {

		return complexPropertyExpression.getName();
	}

	@Override
	public Object visitLambdaFunctionExpression(
			LambdaFunctionExpression.Type type, String variable,
			Expression expression)
		throws ExpressionVisitException {

		if (type == LambdaFunctionExpression.Type.ANY) {
			return _any(expression);
		}

		throw new UnsupportedOperationException(
			"Unsupported type visitLambdaFunctionExpression with type " + type);
	}

	@Override
	public EntityField visitLambdaVariableExpression(
		LambdaVariableExpression lambdaVariableExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		EntityField entityField = entityFieldsMap.get(
			lambdaVariableExpression.getVariableName());

		if (entityField == null) {
			throw new IllegalStateException(
				"VisitLambdaVariableExpression invoked when no entity field " +
					"is stored for lambda variable name " +
						lambdaVariableExpression.getVariableName());
		}

		return entityField;
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression) {
		if (Objects.equals(
				LiteralExpression.Type.DATE, literalExpression.getType())) {

			return _normalizeDateLiteral(literalExpression.getText());
		}
		else if (Objects.equals(
					LiteralExpression.Type.STRING,
					literalExpression.getType())) {

			return _normalizeStringLiteral(literalExpression.getText());
		}

		return literalExpression.getText();
	}

	@Override
	public Object visitMemberExpression(MemberExpression memberExpression)
		throws ExpressionVisitException {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		List<Expression> expressions = memberExpression.getExpressions();

		Optional<ComplexEntityField> complexEntityFieldOptional =
			Optional.empty();

		Optional<EntityField> primitiveEntityFieldOptional = Optional.empty();

		for (Expression expression : expressions) {
			if (expression instanceof ComplexPropertyExpression) {
				complexEntityFieldOptional = Optional.of(
					(ComplexEntityField)entityFieldsMap.get(
						((ComplexPropertyExpression)expression).getName()));
			}
			else if (expression instanceof PrimitivePropertyExpression) {
				PrimitivePropertyExpression primitivePropertyExpression =
					(PrimitivePropertyExpression)expression;

				if (complexEntityFieldOptional.isPresent()) {
					ComplexEntityField complexEntityField =
						complexEntityFieldOptional.get();

					Map<String, EntityField> primitiveEntityFieldsMap =
						complexEntityField.getEntityFieldsMap();

					EntityField primitiveEntityField =
						primitiveEntityFieldsMap.get(
							primitivePropertyExpression.getName());

					primitiveEntityFieldOptional = Optional.of(
						primitiveEntityField);
				}
				else {
					primitiveEntityFieldOptional = Optional.of(
						entityFieldsMap.get(
							primitivePropertyExpression.getName()));
				}
			}
			else if (expression instanceof LambdaVariableExpression) {
				return expression.accept(this);
			}
			else if (expression instanceof LambdaFunctionExpression) {
				if (!primitiveEntityFieldOptional.isPresent()) {
					throw new UnsupportedOperationException(
						"Unsupported empty primitive property in member " +
							"expression " + memberExpression);
				}

				EntityField primitiveEntityField =
					primitiveEntityFieldOptional.get();

				if (!Objects.equals(
						EntityField.Type.COLLECTION,
						primitiveEntityField.getType())) {

					throw new UnsupportedOperationException(
						"Unsupported lambda function expression on " +
							"primitiveEntityField which is not a collection " +
								primitiveEntityField);
				}

				CollectionEntityField collectionEntityField =
					(CollectionEntityField)primitiveEntityField;

				LambdaFunctionExpression lambdaFunctionExpression =
					(LambdaFunctionExpression)expression;

				EntityModel lambdaEntityModel = new EntityModel() {

					@Override
					public Map<String, EntityField> getEntityFieldsMap() {
						return new HashMap<String, EntityField>() {
							{
								put(
									lambdaFunctionExpression.getVariableName(),
									collectionEntityField.getEntityField());
							}
						};
					}

					@Override
					public String getName() {
						return collectionEntityField.getName();
					}

				};

				return expression.accept(
					new ExpressionVisitorImpl(
						_format, _locale, lambdaEntityModel));
			}
			else {
				throw new UnsupportedOperationException(
					"Unsupported member expression with expression " +
						expression.getClass());
			}
		}

		return primitiveEntityFieldOptional.orElseThrow(
			() -> new UnsupportedOperationException(
				"Unsupported member expression " + memberExpression)
		);
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

			return _contains(
				(EntityField)expressions.get(0), expressions.get(1), _locale);
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitMethodExpression with method type " +
				type);
	}

	@Override
	public Object visitPrimitivePropertyExpression(
		PrimitivePropertyExpression primitivePropertyExpression) {

		return primitivePropertyExpression.getName();
	}

	private Object _any(Expression expression) throws ExpressionVisitException {
		return expression.accept(this);
	}

	private Filter _contains(
		EntityField entityField, Object fieldValue, Locale locale) {

		return new QueryFilter(
			new WildcardQueryImpl(
				entityField.getFilterableName(locale),
				"*" + entityField.getFilterableValue(fieldValue) + "*"));
	}

	private Filter _getANDFilter(Filter leftFilter, Filter rightFilter) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(leftFilter, BooleanClauseOccur.MUST);
		booleanFilter.add(rightFilter, BooleanClauseOccur.MUST);

		return booleanFilter;
	}

	private Filter _getEQFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		return new TermFilter(
			entityField.getFilterableName(locale),
			entityField.getFilterableValue(fieldValue));
	}

	private Optional<Filter> _getFilterOptional(
		BinaryExpression.Operation operation, Object left, Object right,
		Locale locale) {

		Filter filter = null;

		if (Objects.equals(BinaryExpression.Operation.AND, operation)) {
			filter = _getANDFilter((Filter)left, (Filter)right);
		}
		else if (Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			filter = _getEQFilter((EntityField)left, right, locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.GE, operation)) {
			filter = _getGEFilter((EntityField)left, right, locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.GT, operation)) {
			filter = _getGTFilter((EntityField)left, right, locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.LE, operation)) {
			filter = _getLEFilter((EntityField)left, right, locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.LT, operation)) {
			filter = _getLTFilter((EntityField)left, right, locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.OR, operation)) {
			filter = _getORFilter((Filter)left, (Filter)right);
		}
		else {
			return Optional.empty();
		}

		return Optional.of(filter);
	}

	private Filter _getGEFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new RangeTermFilter(
				entityField.getFilterableName(locale), true, true,
				String.valueOf(fieldValue), null);
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getGEFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getGTFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new RangeTermFilter(
				entityField.getFilterableName(locale), false, true,
				String.valueOf(fieldValue), null);
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getGTFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getLEFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new RangeTermFilter(
				entityField.getFilterableName(locale), false, true, null,
				String.valueOf(fieldValue));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getLEFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getLTFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new RangeTermFilter(
				entityField.getFilterableName(locale), false, false, null,
				String.valueOf(fieldValue));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getLTFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getORFilter(Filter leftFilter, Filter rightFilter) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(leftFilter, BooleanClauseOccur.SHOULD);
		booleanFilter.add(rightFilter, BooleanClauseOccur.SHOULD);

		return booleanFilter;
	}

	private Object _normalizeDateLiteral(String literal) {
		try {
			Date date = ISO8601Utils.parse(literal, new ParsePosition(0));

			return _format.format(date);
		}
		catch (ParseException pe) {
			throw new InvalidFilterException(
				"Invalid date format, use ISO 8601: " + pe.getMessage());
		}
	}

	private Object _normalizeStringLiteral(String literal) {
		literal = StringUtil.toLowerCase(literal);

		literal = StringUtil.unquote(literal);

		return StringUtil.replace(
			literal, StringPool.DOUBLE_APOSTROPHE, StringPool.APOSTROPHE);
	}

	private final EntityModel _entityModel;
	private final Format _format;
	private final Locale _locale;

}