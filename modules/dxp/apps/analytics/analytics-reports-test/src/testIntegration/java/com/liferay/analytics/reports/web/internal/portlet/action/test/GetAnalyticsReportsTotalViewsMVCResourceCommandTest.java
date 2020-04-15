/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.analytics.reports.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class GetAnalyticsReportsTotalViewsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addLayout(_group);
	}

	@Test
	public void testServeResponse() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_http", _getMockHttp(() -> "12345"));

		try {
			MockResourceResponse mockResourceResponse =
				new MockResourceResponse();

			_mvcResourceCommand.serveResource(
				new MockResourceRequest(), mockResourceResponse);

			ByteArrayOutputStream byteArrayOutputStream =
				(ByteArrayOutputStream)
					mockResourceResponse.getPortletOutputStream();

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				new String(byteArrayOutputStream.toByteArray()));

			Assert.assertEquals(
				12345L, jsonObject.getLong("analyticsReportsTotalViews"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http", _http);
		}
	}

	private Http _getMockHttp(
		UnsafeSupplier<String, Exception> unsafeSupplier) {

		return (Http)ProxyUtil.newProxyInstance(
			Http.class.getClassLoader(), new Class<?>[] {Http.class},
			(proxy, method, args) -> {
				if (!Objects.equals(method.getName(), "URLtoString")) {
					throw new UnsupportedOperationException();
				}

				Http.Options options = (Http.Options)args[0];

				Http.Response response = new Http.Response();

				response.setResponseCode(200);

				options.setResponse(response);

				return unsafeSupplier.get();
			});
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Http _http;

	private Layout _layout;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject(filter = "mvc.command.name=/analytics_reports/get_total_views")
	private MVCResourceCommand _mvcResourceCommand;

	private static class MockResourceResponse
		extends MockLiferayResourceResponse {

		public MockResourceResponse() {
			_mockHttpServletResponse = new MockHttpServletResponse();

			_byteArrayOutputStream = new ByteArrayOutputStream();
		}

		@Override
		public HttpServletResponse getHttpServletResponse() {
			return _mockHttpServletResponse;
		}

		@Override
		public OutputStream getPortletOutputStream() throws IOException {
			return _byteArrayOutputStream;
		}

		private final ByteArrayOutputStream _byteArrayOutputStream;
		private final MockHttpServletResponse _mockHttpServletResponse;

	}

	private class MockResourceRequest extends MockLiferayResourceRequest {

		public MockResourceRequest() {
			_mockHttpServletRequest = new MockHttpServletRequest();

			try {
				_mockHttpServletRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay());
			}
			catch (PortalException portalException) {
				throw new AssertionError(portalException);
			}
		}

		@Override
		public Object getAttribute(String name) {
			if (name.equals(JavaConstants.JAVAX_PORTLET_CONFIG)) {
				return ProxyUtil.newProxyInstance(
					LiferayPortletConfig.class.getClassLoader(),
					new Class<?>[] {LiferayPortletConfig.class},
					(proxy, method, args) -> {
						if (Objects.equals(method.getName(), "getPortletId")) {
							return "testPortlet";
						}

						return null;
					});
			}

			return super.getAttribute(name);
		}

		@Override
		public HttpServletRequest getHttpServletRequest() {
			return _mockHttpServletRequest;
		}

		private ThemeDisplay _getThemeDisplay() throws PortalException {
			ThemeDisplay themeDisplay = new ThemeDisplay();

			Company company = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());

			themeDisplay.setCompany(
				_companyLocalService.getCompany(
					TestPropsValues.getCompanyId()));

			themeDisplay.setLanguageId(_group.getDefaultLanguageId());
			themeDisplay.setLocale(
				LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
			themeDisplay.setLayout(_layout);
			themeDisplay.setLayoutSet(
				_layoutSetLocalService.getLayoutSet(
					_group.getGroupId(), false));
			themeDisplay.setPortalURL(
				company.getPortalURL(_group.getGroupId()));
			themeDisplay.setPortalDomain("localhost");
			themeDisplay.setSecure(true);
			themeDisplay.setServerName("localhost");
			themeDisplay.setServerPort(8080);
			themeDisplay.setSiteGroupId(_group.getGroupId());

			return themeDisplay;
		}

		private final MockHttpServletRequest _mockHttpServletRequest;

	}

}