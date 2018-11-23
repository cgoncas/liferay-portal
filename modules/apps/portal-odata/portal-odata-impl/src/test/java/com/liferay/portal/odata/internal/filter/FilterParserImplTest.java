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

import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.DateEntityField;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.ComplexPropertyExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.LambdaFunctionExpression;
import com.liferay.portal.odata.filter.expression.LambdaVariableExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author David Arques
 */
public class FilterParserImplTest {

	@Test
	public void testParseNonexistingField() {
		String filterString = "(nonExistingField eq 'value')";

		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(filterString)
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Unknown property.");
	}

	@Test
	public void testParseWithContainsMethod() throws ExpressionVisitException {
		Expression expression = _filterParserImpl.parse(
			"contains(fieldExternal, 'value')");

		Assert.assertNotNull(expression);

		MethodExpression methodExpression = (MethodExpression)expression;

		Assert.assertEquals(
			MethodExpression.Type.CONTAINS, methodExpression.getType());

		List<Expression> expressions = methodExpression.getExpressions();

		MemberExpression memberExpression = (MemberExpression)expressions.get(
			0);

		List<Expression> memberExpressions = memberExpression.getExpressions();

		Assert.assertEquals(
			memberExpressions.toString(), 1, memberExpressions.size());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpressions.get(0);

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)expressions.get(1);

		Assert.assertEquals("'value'", literalExpression.getText());
	}

	@Test
	public void testParseWithContainsMethodAndDateType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(
				"contains(dateExternal, 2012-05-29T09:13:28Z)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithContainsMethodAndDoubleType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("contains(doubleExternal, 7)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithEmptyFilter() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Filter is null");
	}

	@Test
	public void testParseWithEqBinaryExpressionOnCollectionField() {
		try {
			_filterParserImpl.parse("collectionFieldExternal eq 'value'");
			Assert.fail("Expected ExpressionVisitException was not thrown");
		}
		catch (ExpressionVisitException eve) {
			Assert.assertEquals("Collection not allowed.", eve.getMessage());
		}
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDate() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("dateExternal ge 2012-05-29")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessageContaining("Incompatible types");
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDateTimeOffset()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"dateExternal ge 2012-05-29T09:13:28Z");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.GE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		List<Expression> expressions = memberExpression.getExpressions();

		Assert.assertEquals(expressions.toString(), 1, expressions.size());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(0);

		Assert.assertEquals(
			"dateExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals(
			"2012-05-29T09:13:28Z", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.DATE, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithMemberWithComplexProperty()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"complexField/primitiveField eq 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		List<Expression> expressions = memberExpression.getExpressions();

		Assert.assertEquals(expressions.toString(), 2, expressions.size());

		ComplexPropertyExpression complexPropertyExpression =
			(ComplexPropertyExpression)expressions.get(0);

		Assert.assertEquals(
			"complexField", complexPropertyExpression.getName());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(1);

		Assert.assertEquals(
			"primitiveField", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithNoSingleQuotes() {
		String filterString = "(fieldExternal eq value)";

		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(filterString)
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Unknown property.");
	}

	@Test
	public void testParseWithEqBinaryExpressionWithSingleQuotes()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"fieldExternal eq 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.EQ, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		List<Expression> expressions = memberExpression.getExpressions();

		Assert.assertEquals(expressions.toString(), 1, expressions.size());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(0);

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithSingleQuotesAndParentheses()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"(fieldExternal eq 'value')");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.EQ, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		List<Expression> expressions = memberExpression.getExpressions();

		Assert.assertEquals(expressions.toString(), 1, expressions.size());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(0);

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithGeBinaryExpression()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"fieldExternal ge 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		List<Expression> expressions = memberExpression.getExpressions();

		Assert.assertEquals(expressions.toString(), 1, expressions.size());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(0);

		Assert.assertEquals(
			BinaryExpression.Operation.GE, binaryExpression.getOperation());
		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithLambdaAllOnCollectionField() {
		try {
			_filterParserImpl.parse(
				"collectionFieldExternal/all(f:contains(f,'alu'))");
			Assert.fail(
				"Expected UnsupportedOperationException was not thrown");
		}
		catch (ExpressionVisitException eve) {
			Assert.assertEquals(
				"UriResource of type UriResourceLambdaAll is not supported",
				eve.getMessage());
		}
	}

	@Test
	public void testParseWithLambdaAnyContainsOnCollectionField()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"collectionFieldExternal/any(f:contains(f,'alu'))");

		Assert.assertNotNull(expression);

		MemberExpression memberExpression = (MemberExpression)expression;

		List<Expression> expressions = memberExpression.getExpressions();

		Assert.assertNotNull(expressions);

		Assert.assertEquals(expressions.toString(), 2, expressions.size());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(0);

		Assert.assertEquals(
			"collectionFieldExternal", primitivePropertyExpression.getName());

		LambdaFunctionExpression lambdaFunctionExpression =
			(LambdaFunctionExpression)expressions.get(1);

		Assert.assertEquals(
			LambdaFunctionExpression.Type.ANY,
			lambdaFunctionExpression.getType());
		Assert.assertEquals("f", lambdaFunctionExpression.getVariableName());

		MethodExpression methodExpression =
			(MethodExpression)lambdaFunctionExpression.getExpression();

		Assert.assertEquals(
			MethodExpression.Type.CONTAINS, methodExpression.getType());

		List<Expression> methodExpressionExpressions =
			methodExpression.getExpressions();

		Assert.assertNotNull(methodExpressionExpressions);
		Assert.assertEquals(
			methodExpressionExpressions.toString(), 2,
			methodExpressionExpressions.size());

		MemberExpression methodExpressionMemberExpression =
			(MemberExpression)methodExpressionExpressions.get(0);

		List<Expression> methodExpressionMemberExpressionExpressions =
			methodExpressionMemberExpression.getExpressions();

		Assert.assertNotNull(methodExpressionMemberExpressionExpressions);
		Assert.assertEquals(
			methodExpressionMemberExpressionExpressions.toString(), 1,
			methodExpressionMemberExpressionExpressions.size());

		LambdaVariableExpression lambdaVariableExpression =
			(LambdaVariableExpression)
				methodExpressionMemberExpressionExpressions.get(0);

		Assert.assertNotNull(lambdaVariableExpression);
		Assert.assertEquals("f", lambdaVariableExpression.getVariableName());

		LiteralExpression literalExpression =
			(LiteralExpression)methodExpressionExpressions.get(1);

		Assert.assertNotNull(literalExpression);
		Assert.assertEquals("'alu'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithLambdaAnyContainsOnNoncollectionField() {
		try {
			_filterParserImpl.parse("fieldExternal/any(f:contains(f,'alu'))");
			Assert.fail("Expected ExpressionVisitException was not thrown");
		}
		catch (ExpressionVisitException eve) {
			Assert.assertEquals(
				"Expected token 'QualifiedName' not found.", eve.getMessage());
		}
	}

	@Test
	public void testParseWithLambdaAnyEqOnCollectionFieldInComplexField()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"complexField/primitiveCollectionField/any(f:contains(f,'alu'))");

		Assert.assertNotNull(expression);
	}

	@Test
	public void testParseWithLeBinaryExpression()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"fieldExternal le 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.LE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		List<Expression> expressions = memberExpression.getExpressions();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)expressions.get(0);

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithNullExpression() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(null)
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Filter is null");
	}

	private static final FilterParserImpl _filterParserImpl =
		new FilterParserImpl(
			new EntityModel() {

				@Override
				public Map<String, EntityField> getEntityFieldsMap() {
					return Stream.of(
						new StringEntityField(
							"collectionFieldExternal", true,
							locale -> "collectionFieldInternal"),
						new ComplexEntityField(
							"complexField",
							Stream.of(
								new StringEntityField(
									"primitiveField", false,
									locale -> "primitiveFieldInternal"),
								new StringEntityField(
									"primitiveCollectionField", true,
									locale ->
										"primitiveCollectionFieldInternal")
							).collect(
								Collectors.toList()
							)),
						new DateEntityField(
							"dateExternal", locale -> "dateInternal",
							locale -> "dateInternal"),
						new DoubleEntityField(
							"doubleExternal", locale -> "doubleInternal"),
						new StringEntityField(
							"fieldExternal", false, locale -> "fieldInternal")
					).collect(
						Collectors.toMap(
							EntityField::getName, Function.identity())
					);
				}

				@Override
				public String getName() {
					return "SomeEntityName";
				}

			});

}